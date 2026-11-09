package com.esporteclubepelotas.api.modules.symbol.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.storage.MediaUploadSanitizer;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.symbol.dto.ClubSymbolOverviewRequest;
import com.esporteclubepelotas.api.modules.symbol.dto.ClubSymbolOverviewResponse;
import com.esporteclubepelotas.api.modules.symbol.dto.ClubSymbolRequest;
import com.esporteclubepelotas.api.modules.symbol.dto.ClubSymbolResponse;
import com.esporteclubepelotas.api.modules.symbol.entity.ClubSymbol;
import com.esporteclubepelotas.api.modules.symbol.entity.ClubSymbolOverview;
import com.esporteclubepelotas.api.modules.symbol.entity.SymbolCategory;
import com.esporteclubepelotas.api.modules.symbol.repository.ClubSymbolOverviewRepository;
import com.esporteclubepelotas.api.modules.symbol.repository.ClubSymbolRepository;
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
public class ClubSymbolService {

    private final ClubSymbolRepository symbolRepository;
    private final ClubSymbolOverviewRepository overviewRepository;
    private final StorageService storageService;
    private final PortalWebSocketHandler webSocketHandler;

    @Transactional(readOnly = true)
    public List<ClubSymbolResponse> listAllForPublic() {
        return symbolRepository.findByIsActiveTrueAndDeletedAtIsNullOrderByCategoryAscDisplayOrderAscYearStartAscCreatedAtDesc()
                .stream()
                .map(ClubSymbolResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClubSymbolResponse> listPublicByCategory(SymbolCategory category) {
        return symbolRepository.findByCategoryAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAscYearStartAscCreatedAtDesc(category)
                .stream()
                .map(ClubSymbolResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClubSymbolResponse getOfficialByCategory(SymbolCategory category) {
        return symbolRepository.findFirstByCategoryAndIsOfficialTrueAndDeletedAtIsNullAndIsActiveTrue(category)
                .or(() -> symbolRepository.findByCategoryAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAscYearStartAscCreatedAtDesc(category).stream().findFirst())
                .map(ClubSymbolResponse::fromEntity)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public ClubSymbolResponse getById(UUID id) {
        ClubSymbol symbol = symbolRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Símbolo não encontrado com id: " + id));
        return ClubSymbolResponse.fromEntity(symbol);
    }

    @Transactional(readOnly = true)
    public List<ClubSymbolResponse> listAllForAdmin() {
        return symbolRepository.findByDeletedAtIsNullOrderByCategoryAscDisplayOrderAscYearStartAscCreatedAtDesc()
                .stream()
                .map(ClubSymbolResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClubSymbolResponse> listAdminByCategory(SymbolCategory category) {
        return symbolRepository.findByCategoryAndDeletedAtIsNullOrderByDisplayOrderAscYearStartAscCreatedAtDesc(category)
                .stream()
                .map(ClubSymbolResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ClubSymbolResponse createSymbol(ClubSymbolRequest request) {
        String cleanImage = MediaUploadSanitizer.sanitizeNullableUrl(request.getImageUrl());
        List<String> cleanGallery = MediaUploadSanitizer.sanitizeUrlList(request.getGalleryUrls());

        boolean isOfficial = Boolean.TRUE.equals(request.getIsOfficial());
        String resolvedName = request.getName() != null && !request.getName().trim().isEmpty()
                ? request.getName().trim()
                : resolveDefaultName(request.getCategory(), request.getYearStart(), request.getYearEnd());

        ClubSymbol symbol = ClubSymbol.builder()
                .category(request.getCategory())
                .name(resolvedName)
                .subtitle(request.getSubtitle() != null ? request.getSubtitle().trim() : null)
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .imageUrl(cleanImage)
                .galleryUrls(cleanGallery)
                .yearStart(request.getYearStart())
                .yearEnd(request.getYearEnd())
                .isOfficial(isOfficial)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        ClubSymbol saved = symbolRepository.save(symbol);

        if (isOfficial) {
            symbolRepository.unsetOtherOfficialSymbols(saved.getCategory(), saved.getId());
        }

        webSocketHandler.broadcastAfterCommit("symbols", "CREATE", saved.getId().toString());

        log.info("Novo símbolo criado com sucesso: id={}, category={}, name={}", saved.getId(), saved.getCategory(), saved.getName());
        return ClubSymbolResponse.fromEntity(saved);
    }

    @Transactional
    public ClubSymbolResponse updateSymbol(UUID id, ClubSymbolRequest request) {
        ClubSymbol symbol = symbolRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Símbolo não encontrado com id: " + id));

        String cleanImage = MediaUploadSanitizer.sanitizeNullableUrl(request.getImageUrl());
        List<String> cleanGallery = MediaUploadSanitizer.sanitizeUrlList(request.getGalleryUrls());

        boolean isOfficial = Boolean.TRUE.equals(request.getIsOfficial());
        String resolvedName = request.getName() != null && !request.getName().trim().isEmpty()
                ? request.getName().trim()
                : resolveDefaultName(request.getCategory(), request.getYearStart(), request.getYearEnd());

        symbol.setCategory(request.getCategory());
        symbol.setName(resolvedName);
        symbol.setSubtitle(request.getSubtitle() != null ? request.getSubtitle().trim() : null);
        symbol.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        symbol.setImageUrl(cleanImage);
        symbol.setGalleryUrls(cleanGallery);
        symbol.setYearStart(request.getYearStart());
        symbol.setYearEnd(request.getYearEnd());
        symbol.setIsOfficial(isOfficial);
        symbol.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        symbol.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        ClubSymbol updated = symbolRepository.save(symbol);

        if (isOfficial) {
            symbolRepository.unsetOtherOfficialSymbols(updated.getCategory(), updated.getId());
        }

        webSocketHandler.broadcastAfterCommit("symbols", "UPDATE", updated.getId().toString());

        log.info("Símbolo atualizado com sucesso: id={}, category={}, name={}", updated.getId(), updated.getCategory(), updated.getName());
        return ClubSymbolResponse.fromEntity(updated);
    }

    @Transactional
    public ClubSymbolResponse setOfficial(UUID id) {
        ClubSymbol symbol = symbolRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Símbolo não encontrado com id: " + id));

        symbolRepository.unsetOtherOfficialSymbols(symbol.getCategory(), symbol.getId());
        symbol.setIsOfficial(true);
        symbol.setIsActive(true);
        ClubSymbol saved = symbolRepository.save(symbol);

        webSocketHandler.broadcastAfterCommit("symbols", "SET_OFFICIAL", saved.getId().toString());

        log.info("Símbolo definido como oficial: id={}, category={}, name={}", saved.getId(), saved.getCategory(), saved.getName());
        return ClubSymbolResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteSymbol(UUID id) {
        ClubSymbol symbol = symbolRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Símbolo não encontrado com id: " + id));

        symbol.setDeletedAt(Instant.now());
        symbol.setIsActive(false);
        symbol.setIsOfficial(false);
        symbolRepository.save(symbol);

        webSocketHandler.broadcastAfterCommit("symbols", "DELETE", symbol.getId().toString());

        log.info("Símbolo arquivado via soft-delete: id={}, name={}", id, symbol.getName());
    }

    public Map<String, String> uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo de imagem foi enviado.");
        }

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "symbol.png";
        String ext = MediaUploadSanitizer.extractSafeExtension(originalName, "png");

        String filename = "symbol-" + UUID.randomUUID() + "." + ext;
        String path = "symbols/" + filename;

        String url = MediaUploadSanitizer.uploadAndVerify(
                file,
                path,
                storageService,
                "Falha ao enviar imagem do símbolo para o armazenamento."
        );

        return Map.of("url", url);
    }

    @Transactional(readOnly = true)
    public List<ClubSymbolOverviewResponse> listAllOverviews() {
        return overviewRepository.findAll().stream()
                .map(ClubSymbolOverviewResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClubSymbolOverviewResponse getOverviewByCategory(String category) {
        return overviewRepository.findById(category.toUpperCase())
                .map(ClubSymbolOverviewResponse::fromEntity)
                .orElseGet(() -> ClubSymbolOverviewResponse.builder()
                        .category(category.toUpperCase())
                        .title("")
                        .description("")
                        .build());
    }

    @Transactional
    public ClubSymbolOverviewResponse updateOverview(String category, ClubSymbolOverviewRequest request) {
        String catKey = category.toUpperCase();
        ClubSymbolOverview overview = overviewRepository.findById(catKey)
                .orElseGet(() -> ClubSymbolOverview.builder()
                        .category(catKey)
                        .build());

        overview.setTitle(request.getTitle().trim());
        overview.setDescription(request.getDescription().trim());
        ClubSymbolOverview saved = overviewRepository.save(overview);

        webSocketHandler.broadcastAfterCommit("symbols_overview", "UPDATE", catKey);
        log.info("Visão geral de símbolo atualizada: category={}, title={}", catKey, saved.getTitle());
        return ClubSymbolOverviewResponse.fromEntity(saved);
    }

    private String resolveDefaultName(SymbolCategory category, Integer yearStart, Integer yearEnd) {
        String prefix = switch (category) {
            case MASCOT -> "Mascote";
            case SHIELD -> "Escudo";
            case FLAG -> "Pavilhão";
            case COLORS -> "Cores";
        };
        if (yearStart != null && yearEnd != null) {
            return prefix + " (" + yearStart + " - " + yearEnd + ")";
        } else if (yearStart != null) {
            return prefix + " (Desde " + yearStart + ")";
        }
        return prefix + " Histórico";
    }
}

