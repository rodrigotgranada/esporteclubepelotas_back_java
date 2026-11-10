package com.esporteclubepelotas.api.modules.match.service;

import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.storage.MediaUploadSanitizer;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.match.dto.MatchRequest;
import com.esporteclubepelotas.api.modules.match.dto.MatchResponse;
import com.esporteclubepelotas.api.modules.match.entity.Championship;
import com.esporteclubepelotas.api.modules.match.entity.HomeAway;
import com.esporteclubepelotas.api.modules.match.entity.Match;
import com.esporteclubepelotas.api.modules.match.entity.MatchStatus;
import com.esporteclubepelotas.api.modules.match.repository.ChampionshipRepository;
import com.esporteclubepelotas.api.modules.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    public static final String DEFAULT_OPPONENT_LOGO = "/images/default-club-badge.svg";

    private final MatchRepository matchRepository;
    private final ChampionshipRepository championshipRepository;
    private final StorageService storageService;
    private final PortalWebSocketHandler webSocketHandler;

    @Transactional(readOnly = true)
    public List<MatchResponse> findAllActive() {
        return matchRepository.findAllActive().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> findByChampionship(UUID championshipId) {
        return matchRepository.findByChampionshipId(championshipId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MatchResponse findById(UUID id) {
        Match match = matchRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada"));
        return toResponse(match);
    }

    @Transactional(readOnly = true)
    public MatchResponse findBySlug(String slug) {
        Match match = matchRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada para o slug: " + slug));
        return toResponse(match);
    }

    /**
     * Retorna a próxima partida do Pelotas (ou em andamento) para destaque na Home.
     * Retorna null se não houver campeonato ativo com jogo agendado.
     */
    @Transactional(readOnly = true)
    public Optional<MatchResponse> findNextMatch() {
        return matchRepository.findNextMatch(Instant.now())
                .map(this::toResponse);
    }

    @Transactional
    public MatchResponse create(MatchRequest request) {
        Championship championship = championshipRepository.findActiveById(request.getChampionshipId())
                .orElseThrow(() -> new ResourceNotFoundException("Campeonato não encontrado"));

        String sanitizedLogo = MediaUploadSanitizer.sanitizeNullableUrl(request.getOpponentLogoUrl());
        if (sanitizedLogo == null || sanitizedLogo.isBlank() || "/images/pelotas-logo.png".equals(sanitizedLogo.trim())) {
            sanitizedLogo = DEFAULT_OPPONENT_LOGO;
        }

        HomeAway homeAway = request.getHomeAway() != null ? request.getHomeAway() : HomeAway.HOME;
        String slug = generateMatchSlug(homeAway, request.getOpponentName(), request.getMatchDate(), request.getSlug(), null);

        Match match = Match.builder()
                .championship(championship)
                .opponentName(request.getOpponentName().trim())
                .opponentLogoUrl(sanitizedLogo)
                .homeAway(homeAway)
                .stadiumName(request.getStadiumName().trim())
                .city(request.getCity() != null ? request.getCity().trim() : null)
                .matchDate(request.getMatchDate())
                .roundName(request.getRoundName().trim())
                .slug(slug)
                .hasLiveCoverage(request.getHasLiveCoverage() != null ? request.getHasLiveCoverage() : false)
                .status(request.getStatus() != null ? request.getStatus() : MatchStatus.SCHEDULED)
                .pelotasScore(request.getPelotasScore())
                .opponentScore(request.getOpponentScore())
                .penaltyPelotasScore(request.getPenaltyPelotasScore())
                .penaltyOpponentScore(request.getPenaltyOpponentScore())
                .ticketUrl(request.getTicketUrl() != null ? request.getTicketUrl().trim() : null)
                .broadcastChannel(request.getBroadcastChannel() != null ? request.getBroadcastChannel().trim() : null)
                .transmissionUrl(request.getTransmissionUrl() != null ? request.getTransmissionUrl().trim() : null)
                .notes(request.getNotes() != null ? request.getNotes().trim() : null)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        Match saved = matchRepository.save(match);
        webSocketHandler.broadcastAfterCommit("matches", "MATCH_CREATED", saved.getId().toString());
        log.info("Partida criada com sucesso: Pelotas vs {} ({})", saved.getOpponentName(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public MatchResponse update(UUID id, MatchRequest request) {
        Match match = matchRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada"));

        Championship championship = championshipRepository.findActiveById(request.getChampionshipId())
                .orElseThrow(() -> new ResourceNotFoundException("Campeonato não encontrado"));

        String sanitizedLogo = MediaUploadSanitizer.sanitizeNullableUrl(request.getOpponentLogoUrl());
        if (sanitizedLogo == null || sanitizedLogo.isBlank() || "/images/pelotas-logo.png".equals(sanitizedLogo.trim())) {
            sanitizedLogo = DEFAULT_OPPONENT_LOGO;
        }

        match.setChampionship(championship);
        match.setOpponentName(request.getOpponentName().trim());
        match.setOpponentLogoUrl(sanitizedLogo);
        if (request.getHomeAway() != null) {
            match.setHomeAway(request.getHomeAway());
        }
        match.setStadiumName(request.getStadiumName().trim());
        match.setCity(request.getCity() != null ? request.getCity().trim() : null);
        match.setMatchDate(request.getMatchDate());
        match.setRoundName(request.getRoundName().trim());

        if (request.getSlug() != null && !request.getSlug().trim().isEmpty() && !request.getSlug().equals(match.getSlug())) {
            match.setSlug(generateMatchSlug(match.getHomeAway(), match.getOpponentName(), match.getMatchDate(), request.getSlug(), match.getId()));
        } else if (match.getSlug() == null || match.getSlug().isBlank()) {
            match.setSlug(generateMatchSlug(match.getHomeAway(), match.getOpponentName(), match.getMatchDate(), null, match.getId()));
        }

        if (request.getHasLiveCoverage() != null) {
            match.setHasLiveCoverage(request.getHasLiveCoverage());
        }

        if (request.getStatus() != null) {
            match.setStatus(request.getStatus());
        }
        match.setPelotasScore(request.getPelotasScore());
        match.setOpponentScore(request.getOpponentScore());
        match.setPenaltyPelotasScore(request.getPenaltyPelotasScore());
        match.setPenaltyOpponentScore(request.getPenaltyOpponentScore());
        match.setTicketUrl(request.getTicketUrl() != null ? request.getTicketUrl().trim() : null);
        match.setBroadcastChannel(request.getBroadcastChannel() != null ? request.getBroadcastChannel().trim() : null);
        match.setTransmissionUrl(request.getTransmissionUrl() != null ? request.getTransmissionUrl().trim() : null);
        match.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);
        if (request.getDisplayOrder() != null) {
            match.setDisplayOrder(request.getDisplayOrder());
        }

        Match updated = matchRepository.save(match);
        webSocketHandler.broadcastAfterCommit("matches", "MATCH_UPDATED", updated.getId().toString());
        log.info("Partida atualizada com sucesso: Pelotas vs {} ({})", updated.getOpponentName(), updated.getId());
        return toResponse(updated);
    }

    @Transactional
    public void delete(UUID id) {
        Match match = matchRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada"));

        match.setDeletedAt(Instant.now());
        matchRepository.save(match);
        webSocketHandler.broadcastAfterCommit("matches", "MATCH_DELETED", id.toString());
        log.info("Partida excluída (soft-delete): Pelotas vs {} ({})", match.getOpponentName(), id);
    }

    /**
     * Upload de escudo do adversário ou logo de campeonato via StorageService
     */
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de imagem não pode ser nulo ou vazio");
        }
        return storageService.uploadFile(file, "matches");
    }

    public MatchResponse toResponse(Match entity) {
        String opponentLogo = entity.getOpponentLogoUrl();
        if (opponentLogo == null || opponentLogo.isBlank() || "/images/pelotas-logo.png".equals(opponentLogo.trim())) {
            opponentLogo = DEFAULT_OPPONENT_LOGO;
        }

        return MatchResponse.builder()
                .id(entity.getId())
                .championshipId(entity.getChampionship() != null ? entity.getChampionship().getId() : null)
                .championshipName(entity.getChampionship() != null ? entity.getChampionship().getName() : null)
                .seasonYear(entity.getChampionship() != null ? entity.getChampionship().getSeasonYear() : null)
                .opponentName(entity.getOpponentName())
                .opponentLogoUrl(opponentLogo)
                .homeAway(entity.getHomeAway())
                .stadiumName(entity.getStadiumName())
                .city(entity.getCity())
                .matchDate(entity.getMatchDate())
                .roundName(entity.getRoundName())
                .slug(entity.getSlug())
                .hasLiveCoverage(entity.getHasLiveCoverage() != null ? entity.getHasLiveCoverage() : false)
                .status(entity.getStatus())
                .pelotasScore(entity.getPelotasScore())
                .opponentScore(entity.getOpponentScore())
                .penaltyPelotasScore(entity.getPenaltyPelotasScore())
                .penaltyOpponentScore(entity.getPenaltyOpponentScore())
                .ticketUrl(entity.getTicketUrl())
                .broadcastChannel(entity.getBroadcastChannel())
                .transmissionUrl(entity.getTransmissionUrl())
                .notes(entity.getNotes())
                .displayOrder(entity.getDisplayOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private String generateMatchSlug(HomeAway homeAway, String opponentName, Instant matchDate, String requestedSlug, UUID excludeId) {
        String base;
        if (requestedSlug != null && !requestedSlug.trim().isEmpty()) {
            base = slugify(requestedSlug.trim());
        } else {
            String dateStr = matchDate != null ? matchDate.toString().substring(0, 10) : "jogo";
            String opClean = slugify(opponentName);
            if (homeAway == HomeAway.HOME) {
                base = "pelotas-vs-" + opClean + "-" + dateStr;
            } else {
                base = opClean + "-vs-pelotas-" + dateStr;
            }
        }

        String candidate = base;
        int counter = 2;
        while (true) {
            java.util.Optional<Match> existing = matchRepository.findBySlugAndDeletedAtIsNull(candidate);
            if (existing.isEmpty() || (excludeId != null && existing.get().getId().equals(excludeId))) {
                return candidate;
            }
            candidate = base + "-" + counter;
            counter++;
        }
    }

    private String slugify(String text) {
        if (text == null) return "";
        return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }
}
