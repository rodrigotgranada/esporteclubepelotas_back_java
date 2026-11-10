package com.esporteclubepelotas.api.modules.match.service;

import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.storage.MediaUploadSanitizer;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.match.dto.ChampionshipRequest;
import com.esporteclubepelotas.api.modules.match.dto.ChampionshipResponse;
import com.esporteclubepelotas.api.modules.match.entity.Championship;
import com.esporteclubepelotas.api.modules.match.entity.ChampionshipCategory;
import com.esporteclubepelotas.api.modules.match.repository.ChampionshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChampionshipService {

    private final ChampionshipRepository championshipRepository;
    private final PortalWebSocketHandler webSocketHandler;

    @Transactional(readOnly = true)
    public List<ChampionshipResponse> findAllActive() {
        return championshipRepository.findAllActive().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChampionshipResponse> findCurrentActive() {
        return championshipRepository.findCurrentActive().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChampionshipResponse findById(UUID id) {
        Championship championship = championshipRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campeonato não encontrado"));
        return toResponse(championship);
    }

    @Transactional
    public ChampionshipResponse create(ChampionshipRequest request) {
        String sanitizedLogo = MediaUploadSanitizer.sanitizeNullableUrl(request.getLogoUrl());

        Championship championship = Championship.builder()
                .name(request.getName().trim())
                .seasonYear(request.getSeasonYear())
                .category(request.getCategory() != null ? request.getCategory() : ChampionshipCategory.PROFESSIONAL_MEN)
                .logoUrl(sanitizedLogo)
                .tableUrl(request.getTableUrl() != null ? request.getTableUrl().trim() : null)
                .regulations(request.getRegulations() != null ? request.getRegulations() : new ArrayList<>())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        Championship saved = championshipRepository.save(championship);
        webSocketHandler.broadcastAfterCommit("matches", "CHAMPIONSHIP_CREATED", saved.getId().toString());
        log.info("Campeonato criado com sucesso: {} ({})", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public ChampionshipResponse update(UUID id, ChampionshipRequest request) {
        Championship championship = championshipRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campeonato não encontrado"));

        String sanitizedLogo = MediaUploadSanitizer.sanitizeNullableUrl(request.getLogoUrl());

        championship.setName(request.getName().trim());
        championship.setSeasonYear(request.getSeasonYear());
        if (request.getCategory() != null) {
            championship.setCategory(request.getCategory());
        }
        championship.setLogoUrl(sanitizedLogo);
        championship.setTableUrl(request.getTableUrl() != null ? request.getTableUrl().trim() : null);
        if (request.getRegulations() != null) {
            championship.setRegulations(request.getRegulations());
        }
        if (request.getIsActive() != null) {
            championship.setIsActive(request.getIsActive());
        }
        if (request.getDisplayOrder() != null) {
            championship.setDisplayOrder(request.getDisplayOrder());
        }

        Championship updated = championshipRepository.save(championship);
        webSocketHandler.broadcastAfterCommit("matches", "CHAMPIONSHIP_UPDATED", updated.getId().toString());
        log.info("Campeonato atualizado com sucesso: {} ({})", updated.getName(), updated.getId());
        return toResponse(updated);
    }

    @Transactional
    public void delete(UUID id) {
        Championship championship = championshipRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campeonato não encontrado"));

        championship.setDeletedAt(Instant.now());
        championshipRepository.save(championship);
        webSocketHandler.broadcastAfterCommit("matches", "CHAMPIONSHIP_DELETED", id.toString());
        log.info("Campeonato excluído (soft-delete): {} ({})", championship.getName(), id);
    }

    public ChampionshipResponse toResponse(Championship entity) {
        return ChampionshipResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .seasonYear(entity.getSeasonYear())
                .category(entity.getCategory())
                .logoUrl(entity.getLogoUrl())
                .tableUrl(entity.getTableUrl())
                .regulations(entity.getRegulations() != null ? entity.getRegulations() : List.of())
                .isActive(entity.getIsActive())
                .displayOrder(entity.getDisplayOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
