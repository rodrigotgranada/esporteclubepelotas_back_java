package com.esporteclubepelotas.api.modules.anthem.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.storage.MediaUploadSanitizer;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.anthem.dto.ClubAnthemRequest;
import com.esporteclubepelotas.api.modules.anthem.dto.ClubAnthemResponse;
import com.esporteclubepelotas.api.modules.anthem.entity.ClubAnthem;
import com.esporteclubepelotas.api.modules.anthem.repository.ClubAnthemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubAnthemService {

    private final ClubAnthemRepository anthemRepository;
    private final StorageService storageService;
    private final PortalWebSocketHandler webSocketHandler;

    @Transactional(readOnly = true)
    public ClubAnthemResponse getOfficialAnthem() {
        return anthemRepository.findFirstByIsOfficialTrueAndDeletedAtIsNullAndIsActiveTrue()
                .or(() -> anthemRepository.findFirstByDeletedAtIsNullAndIsActiveTrueOrderByDisplayOrderAscCreatedAtDesc())
                .map(ClubAnthemResponse::fromEntity)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ClubAnthemResponse> listPublicVersions() {
        return anthemRepository.findByDeletedAtIsNullAndIsActiveTrueOrderByDisplayOrderAscCreatedAtDesc()
                .stream()
                .map(ClubAnthemResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClubAnthemResponse> listAllForAdmin() {
        return anthemRepository.findByDeletedAtIsNullOrderByDisplayOrderAscCreatedAtDesc()
                .stream()
                .map(ClubAnthemResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClubAnthemResponse getAnthemById(UUID id) {
        ClubAnthem anthem = anthemRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Versão do hino não encontrada: " + id));
        return ClubAnthemResponse.fromEntity(anthem);
    }

    @Transactional
    public ClubAnthemResponse createAnthem(ClubAnthemRequest request) {
        String cleanAudio = MediaUploadSanitizer.sanitizeNullableUrl(request.getAudioUrl());
        String cleanCover = MediaUploadSanitizer.sanitizeNullableUrl(request.getCoverImageUrl());
        String cleanVideo = MediaUploadSanitizer.sanitizeNullableUrl(request.getVideoUrl());

        boolean isOfficial = Boolean.TRUE.equals(request.getIsOfficial());

        ClubAnthem anthem = ClubAnthem.builder()
                .title(request.getTitle().trim())
                .subtitle(request.getSubtitle() != null ? request.getSubtitle().trim() : null)
                .lyrics(request.getLyrics().trim())
                .authorLyrics(request.getAuthorLyrics() != null ? request.getAuthorLyrics().trim() : null)
                .authorMusic(request.getAuthorMusic() != null ? request.getAuthorMusic().trim() : null)
                .compositionYear(request.getCompositionYear())
                .audioUrl(cleanAudio)
                .videoUrl(cleanVideo)
                .coverImageUrl(cleanCover)
                .historyTitle(request.getHistoryTitle() != null ? request.getHistoryTitle().trim() : null)
                .historyText(request.getHistoryText() != null ? request.getHistoryText().trim() : null)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .isOfficial(isOfficial)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        ClubAnthem saved = anthemRepository.save(anthem);

        if (isOfficial) {
            anthemRepository.unsetOtherOfficialAnthems(saved.getId());
        }

        webSocketHandler.broadcastAfterCommit("anthem", "CREATE", saved.getId().toString());

        log.info("Nova versão do hino cadastrada com sucesso: id={}, title={}", saved.getId(), saved.getTitle());
        return ClubAnthemResponse.fromEntity(saved);
    }

    @Transactional
    public ClubAnthemResponse updateAnthem(UUID id, ClubAnthemRequest request) {
        ClubAnthem anthem = anthemRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Versão do hino não encontrada: " + id));

        String cleanAudio = MediaUploadSanitizer.sanitizeNullableUrl(request.getAudioUrl());
        String cleanCover = MediaUploadSanitizer.sanitizeNullableUrl(request.getCoverImageUrl());
        String cleanVideo = MediaUploadSanitizer.sanitizeNullableUrl(request.getVideoUrl());

        boolean isOfficial = Boolean.TRUE.equals(request.getIsOfficial());

        anthem.setTitle(request.getTitle().trim());
        anthem.setSubtitle(request.getSubtitle() != null ? request.getSubtitle().trim() : null);
        anthem.setLyrics(request.getLyrics().trim());
        anthem.setAuthorLyrics(request.getAuthorLyrics() != null ? request.getAuthorLyrics().trim() : null);
        anthem.setAuthorMusic(request.getAuthorMusic() != null ? request.getAuthorMusic().trim() : null);
        anthem.setCompositionYear(request.getCompositionYear());
        anthem.setAudioUrl(cleanAudio);
        anthem.setVideoUrl(cleanVideo);
        anthem.setCoverImageUrl(cleanCover);
        anthem.setHistoryTitle(request.getHistoryTitle() != null ? request.getHistoryTitle().trim() : null);
        anthem.setHistoryText(request.getHistoryText() != null ? request.getHistoryText().trim() : null);
        anthem.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        anthem.setIsOfficial(isOfficial);
        anthem.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        ClubAnthem updated = anthemRepository.save(anthem);

        if (isOfficial) {
            anthemRepository.unsetOtherOfficialAnthems(updated.getId());
        }

        webSocketHandler.broadcastAfterCommit("anthem", "UPDATE", updated.getId().toString());

        log.info("Versão do hino atualizada com sucesso: id={}, title={}", updated.getId(), updated.getTitle());
        return ClubAnthemResponse.fromEntity(updated);
    }

    @Transactional
    public ClubAnthemResponse setOfficial(UUID id) {
        ClubAnthem anthem = anthemRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Versão do hino não encontrada: " + id));

        anthemRepository.unsetOtherOfficialAnthems(anthem.getId());
        anthem.setIsOfficial(true);
        anthem.setIsActive(true);
        ClubAnthem saved = anthemRepository.save(anthem);

        webSocketHandler.broadcastAfterCommit("anthem", "SET_OFFICIAL", saved.getId().toString());

        log.info("Versão do hino definida como oficial: id={}, title={}", saved.getId(), saved.getTitle());
        return ClubAnthemResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteAnthem(UUID id) {
        ClubAnthem anthem = anthemRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Versão do hino não encontrada: " + id));

        anthem.setDeletedAt(Instant.now());
        anthem.setIsActive(false);
        anthem.setIsOfficial(false);
        anthemRepository.save(anthem);

        webSocketHandler.broadcastAfterCommit("anthem", "DELETE", anthem.getId().toString());

        log.info("Versão do hino removida via soft-delete: id={}", id);
    }

    public Map<String, String> uploadAudio(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo de áudio foi enviado.");
        }

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "audio.mp3";
        String ext = MediaUploadSanitizer.extractSafeExtension(originalName, "mp3");

        String filename = "hino-" + UUID.randomUUID() + "." + ext;
        String path = "anthems/audio/" + filename;

        String url = MediaUploadSanitizer.uploadAndVerify(
                file,
                path,
                storageService,
                "Falha ao enviar arquivo de áudio do hino para o armazenamento."
        );

        return Map.of("url", url);
    }

    public Map<String, String> uploadCover(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo de imagem foi enviado.");
        }

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "cover.jpg";
        String ext = MediaUploadSanitizer.extractSafeExtension(originalName, "jpg");

        String filename = "cover-" + UUID.randomUUID() + "." + ext;
        String path = "anthems/covers/" + filename;

        String url = MediaUploadSanitizer.uploadAndVerify(
                file,
                path,
                storageService,
                "Falha ao enviar imagem de capa do hino para o armazenamento."
        );

        return Map.of("url", url);
    }
}
