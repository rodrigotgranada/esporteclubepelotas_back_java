package com.esporteclubepelotas.api.modules.kit.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.storage.MediaUploadSanitizer;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.kit.dto.ClubKitCompetitionItemResponse;
import com.esporteclubepelotas.api.modules.kit.dto.ClubKitRequest;
import com.esporteclubepelotas.api.modules.kit.dto.ClubKitResponse;
import com.esporteclubepelotas.api.modules.kit.entity.ClubKit;
import com.esporteclubepelotas.api.modules.kit.repository.ClubKitRepository;
import com.esporteclubepelotas.api.modules.match.entity.Championship;
import com.esporteclubepelotas.api.modules.match.repository.ChampionshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubKitService {

    private final ClubKitRepository kitRepository;
    private final ChampionshipRepository championshipRepository;
    private final StorageService storageService;
    private final PortalWebSocketHandler webSocketHandler;

    @Transactional(readOnly = true)
    public List<ClubKitResponse> listAllForPublic() {
        return kitRepository.findByIsActiveTrueAndDeletedAtIsNullOrderBySeasonYearDescDisplayOrderAscCreatedAtDesc()
                .stream()
                .map(ClubKitResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClubKitResponse> listCurrentKits() {
        return kitRepository.findByIsCurrentTrueAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAscCreatedAtAsc()
                .stream()
                .map(ClubKitResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClubKitResponse> listHistoricalKits() {
        return kitRepository.findByIsCurrentFalseAndIsActiveTrueAndDeletedAtIsNullOrderBySeasonYearDescDisplayOrderAscCreatedAtDesc()
                .stream()
                .map(ClubKitResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClubKitResponse> listHistoricalByCompetition(String competition) {
        if (competition == null || competition.trim().isEmpty()) {
            return listHistoricalKits();
        }
        return kitRepository.findByCompetitionIgnoreCaseAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAscCreatedAtAsc(competition.trim())
                .stream()
                .map(ClubKitResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClubKitCompetitionItemResponse> listHistoricalCompetitions() {
        List<Object[]> rawList = kitRepository.findHistoricalCompetitions();
        return rawList.stream().map(row -> {
            String comp = (String) row[0];
            Integer year = (Integer) row[1];
            String label = (comp != null ? comp : "") + (year != null ? " (" + year + ")" : "");
            return ClubKitCompetitionItemResponse.builder()
                    .competition(comp)
                    .seasonYear(year)
                    .label(label)
                    .build();
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<String> listAllDistinctCompetitions() {
        return kitRepository.findAllDistinctCompetitions();
    }

    @Transactional(readOnly = true)
    public ClubKitResponse getById(UUID id) {
        ClubKit kit = kitRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Uniforme não encontrado com id: " + id));
        return ClubKitResponse.fromEntity(kit);
    }

    @Transactional(readOnly = true)
    public List<ClubKitResponse> listAllForAdmin() {
        return kitRepository.findByDeletedAtIsNullOrderBySeasonYearDescDisplayOrderAscCreatedAtDesc()
                .stream()
                .map(ClubKitResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ClubKitResponse createKit(ClubKitRequest request) {
        String cleanImage = MediaUploadSanitizer.sanitizeNullableUrl(request.getImageUrl());
        if (cleanImage == null || cleanImage.trim().isEmpty()) {
            throw new BusinessException("A imagem principal do uniforme é obrigatória.");
        }
        List<String> cleanGallery = MediaUploadSanitizer.sanitizeUrlList(request.getGalleryUrls());

        Championship championship = resolveChampionship(request.getChampionshipId(), request.getCompetition());
        String compName = championship != null ? championship.getName() : request.getCompetition().trim();
        Integer year = championship != null ? championship.getSeasonYear() : request.getSeasonYear();

        ClubKit kit = ClubKit.builder()
                .name(request.getName().trim())
                .kitType(request.getKitType())
                .championship(championship)
                .competition(compName)
                .seasonYear(year)
                .imageUrl(cleanImage)
                .galleryUrls(cleanGallery)
                .manufacturer(request.getManufacturer() != null ? request.getManufacturer().trim() : null)
                .sponsor(request.getSponsor() != null ? request.getSponsor().trim() : null)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .isCurrent(Boolean.TRUE.equals(request.getIsCurrent()))
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        ClubKit saved = kitRepository.save(kit);

        webSocketHandler.broadcastAfterCommit("kits", "CREATE", saved.getId().toString());
        log.info("Novo uniforme criado com sucesso: id={}, name={}, competition={}, type={}",
                saved.getId(), saved.getName(), saved.getCompetition(), saved.getKitType());

        return ClubKitResponse.fromEntity(saved);
    }

    @Transactional
    public ClubKitResponse updateKit(UUID id, ClubKitRequest request) {
        ClubKit kit = kitRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Uniforme não encontrado com id: " + id));

        String cleanImage = MediaUploadSanitizer.sanitizeNullableUrl(request.getImageUrl());
        if (cleanImage == null || cleanImage.trim().isEmpty()) {
            throw new BusinessException("A imagem principal do uniforme é obrigatória.");
        }
        List<String> cleanGallery = MediaUploadSanitizer.sanitizeUrlList(request.getGalleryUrls());

        Championship championship = resolveChampionship(request.getChampionshipId(), request.getCompetition());
        String compName = championship != null ? championship.getName() : request.getCompetition().trim();
        Integer year = championship != null ? championship.getSeasonYear() : request.getSeasonYear();

        kit.setName(request.getName().trim());
        kit.setKitType(request.getKitType());
        kit.setChampionship(championship);
        kit.setCompetition(compName);
        kit.setSeasonYear(year);
        kit.setImageUrl(cleanImage);
        kit.setGalleryUrls(cleanGallery);
        kit.setManufacturer(request.getManufacturer() != null ? request.getManufacturer().trim() : null);
        kit.setSponsor(request.getSponsor() != null ? request.getSponsor().trim() : null);
        kit.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        kit.setIsCurrent(Boolean.TRUE.equals(request.getIsCurrent()));
        kit.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        kit.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        ClubKit updated = kitRepository.save(kit);

        webSocketHandler.broadcastAfterCommit("kits", "UPDATE", updated.getId().toString());
        log.info("Uniforme atualizado com sucesso: id={}, name={}, competition={}",
                updated.getId(), updated.getName(), updated.getCompetition());

        return ClubKitResponse.fromEntity(updated);
    }

    private Championship resolveChampionship(UUID championshipId, String competitionName) {
        if (championshipId != null) {
            java.util.Optional<Championship> byId = championshipRepository.findActiveById(championshipId);
            if (byId.isPresent()) return byId.get();
        }
        if (competitionName != null && !competitionName.trim().isEmpty()) {
            String clean = competitionName.trim().toLowerCase();
            return championshipRepository.findAllActive().stream()
                    .filter(c -> c.getName().toLowerCase().equals(clean) || clean.contains(c.getName().toLowerCase()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Transactional
    public ClubKitResponse toggleCurrent(UUID id) {
        ClubKit kit = kitRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Uniforme não encontrado com id: " + id));

        kit.setIsCurrent(!Boolean.TRUE.equals(kit.getIsCurrent()));
        ClubKit updated = kitRepository.save(kit);

        webSocketHandler.broadcastAfterCommit("kits", "UPDATE", updated.getId().toString());
        log.info("Status de uniforme atual alterado: id={}, isCurrent={}", updated.getId(), updated.getIsCurrent());

        return ClubKitResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteKit(UUID id) {
        ClubKit kit = kitRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Uniforme não encontrado com id: " + id));

        kit.setDeletedAt(Instant.now());
        kit.setIsActive(false);
        kitRepository.save(kit);

        webSocketHandler.broadcastAfterCommit("kits", "DELETE", id.toString());
        log.info("Uniforme excluído com sucesso (soft-delete): id={}, name={}", id, kit.getName());
    }

    public Map<String, String> uploadKitImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo de imagem foi enviado.");
        }

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "kit.png";
        String ext = MediaUploadSanitizer.extractSafeExtension(originalName, "png");

        String filename = "kit-" + UUID.randomUUID() + "." + ext;
        String path = "kits/" + filename;

        String url = MediaUploadSanitizer.uploadAndVerify(
                file,
                path,
                storageService,
                "Falha ao enviar imagem do uniforme para o armazenamento."
        );

        return Map.of("url", url);
    }
}

