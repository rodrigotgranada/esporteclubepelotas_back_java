package com.esporteclubepelotas.api.modules.opponent.service;

import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.storage.MediaUploadSanitizer;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.opponent.dto.OpponentClubRequest;
import com.esporteclubepelotas.api.modules.opponent.dto.OpponentClubResponse;
import com.esporteclubepelotas.api.modules.opponent.entity.OpponentClub;
import com.esporteclubepelotas.api.modules.opponent.repository.OpponentClubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpponentClubService {

    public static final String DEFAULT_BADGE_URL = "/images/default-club-badge.svg";

    private final OpponentClubRepository clubRepository;
    private final StorageService storageService;
    private final PortalWebSocketHandler webSocketHandler;

    @Transactional(readOnly = true)
    public List<OpponentClubResponse> findAll(String query) {
        List<OpponentClub> list;
        if (query != null && !query.trim().isEmpty()) {
            list = clubRepository.searchClubs(query.trim());
        } else {
            list = clubRepository.findAllActive();
        }
        return list.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OpponentClubResponse findById(UUID id) {
        OpponentClub club = clubRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clube adversário não encontrado"));
        return toResponse(club);
    }

    @Transactional
    public OpponentClubResponse create(OpponentClubRequest request) {
        String sanitizedBadge = MediaUploadSanitizer.sanitizeNullableUrl(request.getBadgeUrl());
        if (sanitizedBadge == null || sanitizedBadge.isBlank() || "/images/pelotas-logo.png".equals(sanitizedBadge.trim())) {
            sanitizedBadge = DEFAULT_BADGE_URL;
        }

        OpponentClub club = OpponentClub.builder()
                .name(request.getName().trim())
                .shortName(request.getShortName() != null ? request.getShortName().trim() : null)
                .badgeUrl(sanitizedBadge)
                .city(request.getCity() != null ? request.getCity().trim() : null)
                .stadiumName(request.getStadiumName() != null ? request.getStadiumName().trim() : null)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        OpponentClub saved = clubRepository.save(club);
        webSocketHandler.broadcastAfterCommit("clubs", "CLUB_CREATED", saved.getId().toString());
        log.info("Clube adversário criado com sucesso: {} ({})", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public OpponentClubResponse update(UUID id, OpponentClubRequest request) {
        OpponentClub club = clubRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clube adversário não encontrado"));

        String sanitizedBadge = MediaUploadSanitizer.sanitizeNullableUrl(request.getBadgeUrl());
        if (sanitizedBadge == null || sanitizedBadge.isBlank() || "/images/pelotas-logo.png".equals(sanitizedBadge.trim())) {
            sanitizedBadge = DEFAULT_BADGE_URL;
        }

        club.setName(request.getName().trim());
        club.setShortName(request.getShortName() != null ? request.getShortName().trim() : null);
        club.setBadgeUrl(sanitizedBadge);
        club.setCity(request.getCity() != null ? request.getCity().trim() : null);
        club.setStadiumName(request.getStadiumName() != null ? request.getStadiumName().trim() : null);
        if (request.getIsActive() != null) {
            club.setIsActive(request.getIsActive());
        }
        if (request.getDisplayOrder() != null) {
            club.setDisplayOrder(request.getDisplayOrder());
        }

        OpponentClub updated = clubRepository.save(club);
        webSocketHandler.broadcastAfterCommit("clubs", "CLUB_UPDATED", updated.getId().toString());
        log.info("Clube adversário atualizado com sucesso: {} ({})", updated.getName(), updated.getId());
        return toResponse(updated);
    }

    @Transactional
    public void delete(UUID id) {
        OpponentClub club = clubRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clube adversário não encontrado"));

        club.setDeletedAt(Instant.now());
        clubRepository.save(club);
        webSocketHandler.broadcastAfterCommit("clubs", "CLUB_DELETED", id.toString());
        log.info("Clube adversário excluído (soft-delete): {} ({})", club.getName(), id);
    }

    public String uploadBadge(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de distintivo não pode ser nulo ou vazio");
        }
        return storageService.uploadFile(file, "clubs");
    }

    public OpponentClubResponse toResponse(OpponentClub entity) {
        String badge = entity.getBadgeUrl();
        if (badge == null || badge.isBlank() || "/images/pelotas-logo.png".equals(badge.trim())) {
            badge = DEFAULT_BADGE_URL;
        }

        return OpponentClubResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .shortName(entity.getShortName())
                .badgeUrl(badge)
                .city(entity.getCity())
                .stadiumName(entity.getStadiumName())
                .isActive(entity.getIsActive())
                .displayOrder(entity.getDisplayOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

