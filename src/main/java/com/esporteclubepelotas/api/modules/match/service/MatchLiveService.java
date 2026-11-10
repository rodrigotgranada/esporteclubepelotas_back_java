package com.esporteclubepelotas.api.modules.match.service;

import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.football.entity.Athlete;
import com.esporteclubepelotas.api.modules.football.repository.AthleteRepository;
import com.esporteclubepelotas.api.modules.match.dto.*;
import com.esporteclubepelotas.api.modules.match.entity.*;
import com.esporteclubepelotas.api.modules.match.repository.MatchEventRepository;
import com.esporteclubepelotas.api.modules.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchLiveService {

    private final MatchRepository matchRepository;
    private final MatchEventRepository matchEventRepository;
    private final MatchService matchService;
    private final AthleteRepository athleteRepository;
    private final PortalWebSocketHandler webSocketHandler;

    @Transactional(readOnly = true)
    public MatchLiveDetailsResponse getLiveDetails(String matchIdOrSlug) {
        Match match = resolveMatch(matchIdOrSlug);
        List<MatchEvent> events = matchEventRepository.findByMatchIdOrderByTimelineDesc(match.getId());

        List<MatchEventResponse> eventResponses = events.stream()
                .map(this::toEventResponse)
                .toList();

        boolean isTimerRunning = match.getStatus() == MatchStatus.IN_PROGRESS && match.getTimerStartedAt() != null;

        return MatchLiveDetailsResponse.builder()
                .match(matchService.toResponse(match))
                .events(eventResponses)
                .currentPeriod(match.getCurrentPeriod() != null ? match.getCurrentPeriod() : "PRE_MATCH")
                .currentMinute(match.getCurrentMinute() != null ? match.getCurrentMinute() : "0'")
                .timerStartedAt(match.getTimerStartedAt())
                .isTimerRunning(isTimerRunning)
                .build();
    }

    @Transactional
    public MatchEventResponse addEvent(UUID matchId, MatchEventRequest request) {
        Match match = matchRepository.findActiveById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada: " + matchId));

        boolean scoreChanged = false;

        // Se o placar atual for nulo, inicializa em 0
        if (match.getPelotasScore() == null) match.setPelotasScore(0);
        if (match.getOpponentScore() == null) match.setOpponentScore(0);

        MatchEventType type = request.getEventType();
        MatchEventTeam team = request.getTeam() != null ? request.getTeam() : MatchEventTeam.NEUTRAL;
        String title = request.getTitle();
        String description = request.getDescription();
        String playerName = request.getPlayerName() != null ? request.getPlayerName().trim() : null;
        String playerOutName = request.getPlayerOutName() != null ? request.getPlayerOutName().trim() : null;

        Athlete playerAthlete = null;
        Athlete playerOutAthlete = null;
        if (team == MatchEventTeam.PELOTAS || type == MatchEventType.GOAL_PELOTAS) {
            playerAthlete = resolveAthlete(request.getPlayerAthleteId(), playerName);
            if (playerAthlete != null) {
                playerName = playerAthlete.getNickname() != null && !playerAthlete.getNickname().isBlank()
                        ? playerAthlete.getNickname()
                        : playerAthlete.getName();
            }
            playerOutAthlete = resolveAthlete(request.getPlayerOutAthleteId(), playerOutName);
            if (playerOutAthlete != null) {
                playerOutName = playerOutAthlete.getNickname() != null && !playerOutAthlete.getNickname().isBlank()
                        ? playerOutAthlete.getNickname()
                        : playerOutAthlete.getName();
            }
        }

        // Lógica automática de títulos e incremento de placar
        switch (type) {
            case GOAL_PELOTAS:
                team = MatchEventTeam.PELOTAS;
                match.setPelotasScore(match.getPelotasScore() + 1);
                scoreChanged = true;
                if (title == null || title.isBlank()) {
                    title = playerName != null && !playerName.isBlank()
                            ? "Gol do Pelotas! (" + playerName + ")"
                            : "Gol do Pelotas!";
                }
                break;

            case GOAL_OPPONENT:
                team = MatchEventTeam.OPPONENT;
                match.setOpponentScore(match.getOpponentScore() + 1);
                scoreChanged = true;
                if (title == null || title.isBlank()) {
                    title = playerName != null && !playerName.isBlank()
                            ? "Gol do " + match.getOpponentName() + " (" + playerName + ")"
                            : "Gol do " + match.getOpponentName();
                }
                break;

            case YELLOW_CARD:
                if (title == null || title.isBlank()) {
                    title = "Cartão Amarelo (" + (team == MatchEventTeam.PELOTAS ? "Pelotas" : match.getOpponentName()) + ")";
                }
                break;

            case RED_CARD:
                if (title == null || title.isBlank()) {
                    title = "Cartão Vermelho (" + (team == MatchEventTeam.PELOTAS ? "Pelotas" : match.getOpponentName()) + ")";
                }
                break;

            case SUBSTITUTION:
                if (title == null || title.isBlank()) {
                    title = "Substituição (" + (team == MatchEventTeam.PELOTAS ? "Pelotas" : match.getOpponentName()) + ")";
                }
                if ((description == null || description.isBlank()) && (playerOutName != null || playerName != null)) {
                    description = "Sai: " + (playerOutName != null ? playerOutName : "-") + " | Entra: " + (playerName != null ? playerName : "-");
                }
                break;

            case DANGEROUS_ATTACK:
                if (title == null || title.isBlank()) title = "Lance de Perigo!";
                break;

            case PENALTY:
                if (title == null || title.isBlank()) title = "Pênalti Marcado!";
                break;

            case VAR:
                if (title == null || title.isBlank()) title = "Revisão do VAR";
                break;

            default:
                if (title == null || title.isBlank()) title = "Lance da Partida";
                break;
        }

        // Determinar período
        MatchPeriod period = request.getPeriod();
        if (period == null) {
            try {
                period = MatchPeriod.valueOf(match.getCurrentPeriod());
            } catch (Exception e) {
                period = MatchPeriod.FIRST_HALF;
            }
        }

        MatchEvent event = MatchEvent.builder()
                .match(match)
                .minute(request.getMinute())
                .minuteExtra(request.getMinuteExtra())
                .period(period)
                .eventType(type)
                .team(team)
                .playerAthlete(playerAthlete)
                .playerOutAthlete(playerOutAthlete)
                .playerName(playerName)
                .playerOutName(playerOutName)
                .title(title)
                .description(description)
                .scorePelotasMoment(match.getPelotasScore())
                .scoreOpponentMoment(match.getOpponentScore())
                .displayOrder(0)
                .build();

        MatchEvent saved = matchEventRepository.save(event);

        if (scoreChanged) {
            matchRepository.save(match);
        }

        MatchEventResponse response = toEventResponse(saved);

        // Disparo duplo no WebSocket
        webSocketHandler.broadcastAfterCommit("match-live-" + match.getId(), "EVENT_ADDED", saved.getId().toString());
        if (scoreChanged) {
            webSocketHandler.broadcastAfterCommit("matches", "MATCH_UPDATED", match.getId().toString());
        }

        log.info("Lance registrado na partida {} vs {}: {} (min {})", match.getId(), match.getOpponentName(), title, request.getMinute());
        return response;
    }

    @Transactional
    public void deleteEvent(UUID eventId) {
        MatchEvent event = matchEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Lance não encontrado: " + eventId));

        Match match = event.getMatch();
        boolean scoreChanged = false;

        // Se deletou um gol, estorna o placar
        if (event.getEventType() == MatchEventType.GOAL_PELOTAS && match.getPelotasScore() != null && match.getPelotasScore() > 0) {
            match.setPelotasScore(match.getPelotasScore() - 1);
            scoreChanged = true;
        } else if (event.getEventType() == MatchEventType.GOAL_OPPONENT && match.getOpponentScore() != null && match.getOpponentScore() > 0) {
            match.setOpponentScore(match.getOpponentScore() - 1);
            scoreChanged = true;
        }

        matchEventRepository.delete(event);

        if (scoreChanged) {
            matchRepository.save(match);
        }

        webSocketHandler.broadcastAfterCommit("match-live-" + match.getId(), "EVENT_DELETED", eventId.toString());
        if (scoreChanged) {
            webSocketHandler.broadcastAfterCommit("matches", "MATCH_UPDATED", match.getId().toString());
        }

        log.info("Lance {} excluído da partida {}", eventId, match.getId());
    }

    @Transactional
    public MatchLiveDetailsResponse handleMatchAction(UUID matchId, MatchLiveActionRequest request) {
        Match match = matchRepository.findActiveById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada: " + matchId));

        String action = request.getAction().toUpperCase();
        Instant now = Instant.now();

        switch (action) {
            case "START_FIRST_HALF":
                match.setStatus(MatchStatus.IN_PROGRESS);
                match.setCurrentPeriod(MatchPeriod.FIRST_HALF.name());
                match.setTimerStartedAt(now);
                match.setCurrentMinute("1'");
                if (match.getPelotasScore() == null) match.setPelotasScore(0);
                if (match.getOpponentScore() == null) match.setOpponentScore(0);

                // Lance automático de início
                createAutomaticEvent(match, 0, null, MatchPeriod.FIRST_HALF, MatchEventType.START_FIRST_HALF,
                        "Começa o 1º Tempo", "Apita o árbitro! Bola rolando no confronto.");
                break;

            case "END_FIRST_HALF":
                match.setCurrentPeriod(MatchPeriod.HALF_TIME.name());
                match.setTimerStartedAt(null);
                match.setCurrentMinute("Int");

                createAutomaticEvent(match, 45, null, MatchPeriod.HALF_TIME, MatchEventType.END_FIRST_HALF,
                        "Fim do 1º Tempo", "Fim da primeira etapa. As equipes vão para o vestiário.");
                break;

            case "START_SECOND_HALF":
                match.setStatus(MatchStatus.IN_PROGRESS);
                match.setCurrentPeriod(MatchPeriod.SECOND_HALF.name());
                match.setTimerStartedAt(now);
                match.setCurrentMinute("46'");

                createAutomaticEvent(match, 45, null, MatchPeriod.SECOND_HALF, MatchEventType.START_SECOND_HALF,
                        "Começa o 2º Tempo", "Bola rolando para os 45 minutos finais.");
                break;

            case "END_SECOND_HALF":
            case "FINISH_MATCH":
                match.setStatus(MatchStatus.FINISHED);
                match.setCurrentPeriod(MatchPeriod.FINISHED.name());
                match.setTimerStartedAt(null);
                match.setCurrentMinute("Fim");

                createAutomaticEvent(match, 90, null, MatchPeriod.FINISHED, MatchEventType.END_MATCH,
                        "Fim de Jogo", "Fim de papo! Apita o árbitro, encerrando a partida.");
                break;

            case "UPDATE_TIMER":
                if (request.getMinute() != null) {
                    match.setCurrentMinute(request.getMinute() + "'");
                }
                break;

            default:
                throw new IllegalArgumentException("Ação de partida inválida: " + action);
        }

        matchRepository.save(match);

        // Notificar tanto os ouvintes do lance a lance quanto o calendário/home
        webSocketHandler.broadcastAfterCommit("match-live-" + match.getId(), "ACTION_PROCESSED", action);
        webSocketHandler.broadcastAfterCommit("matches", "MATCH_UPDATED", match.getId().toString());

        log.info("Ação {} executada na partida {}", action, match.getId());
        return getLiveDetails(match.getId().toString());
    }

    private void createAutomaticEvent(Match match, int minute, Integer minuteExtra, MatchPeriod period, MatchEventType type, String title, String description) {
        MatchEvent autoEvent = MatchEvent.builder()
                .match(match)
                .minute(minute)
                .minuteExtra(minuteExtra)
                .period(period)
                .eventType(type)
                .team(MatchEventTeam.NEUTRAL)
                .title(title)
                .description(description)
                .scorePelotasMoment(match.getPelotasScore())
                .scoreOpponentMoment(match.getOpponentScore())
                .displayOrder(0)
                .build();
        matchEventRepository.save(autoEvent);
    }

    private Match resolveMatch(String matchIdOrSlug) {
        try {
            UUID id = UUID.fromString(matchIdOrSlug);
            return matchRepository.findActiveById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada com ID: " + matchIdOrSlug));
        } catch (IllegalArgumentException e) {
            return matchRepository.findBySlugAndDeletedAtIsNull(matchIdOrSlug)
                    .orElseThrow(() -> new ResourceNotFoundException("Partida não encontrada com slug: " + matchIdOrSlug));
        }
    }

    public MatchEventResponse toEventResponse(MatchEvent event) {
        String formattedMinute = event.getMinute() != null ? event.getMinute() + "'" : "0'";
        if (event.getMinuteExtra() != null && event.getMinuteExtra() > 0) {
            formattedMinute = event.getMinute() + "+" + event.getMinuteExtra() + "'";
        }

        UUID playerAthleteId = null;
        String playerNickname = null;
        String playerPhotoUrl = null;
        String playerName = event.getPlayerName();

        if (event.getPlayerAthlete() != null) {
            Athlete a = event.getPlayerAthlete();
            playerAthleteId = a.getId();
            playerNickname = a.getNickname();
            playerPhotoUrl = a.getPhotoUrl();
            if (playerName == null || playerName.isBlank()) {
                playerName = a.getNickname() != null && !a.getNickname().isBlank() ? a.getNickname() : a.getName();
            }
        }

        UUID playerOutAthleteId = null;
        String playerOutNickname = null;
        String playerOutName = event.getPlayerOutName();

        if (event.getPlayerOutAthlete() != null) {
            Athlete out = event.getPlayerOutAthlete();
            playerOutAthleteId = out.getId();
            playerOutNickname = out.getNickname();
            if (playerOutName == null || playerOutName.isBlank()) {
                playerOutName = out.getNickname() != null && !out.getNickname().isBlank() ? out.getNickname() : out.getName();
            }
        }

        return MatchEventResponse.builder()
                .id(event.getId())
                .matchId(event.getMatch() != null ? event.getMatch().getId() : null)
                .minute(event.getMinute())
                .minuteExtra(event.getMinuteExtra())
                .formattedMinute(formattedMinute)
                .period(event.getPeriod())
                .eventType(event.getEventType())
                .team(event.getTeam())
                .playerAthleteId(playerAthleteId)
                .playerNickname(playerNickname)
                .playerPhotoUrl(playerPhotoUrl)
                .playerOutAthleteId(playerOutAthleteId)
                .playerOutNickname(playerOutNickname)
                .playerName(playerName)
                .playerOutName(playerOutName)
                .title(event.getTitle())
                .description(event.getDescription())
                .scorePelotasMoment(event.getScorePelotasMoment())
                .scoreOpponentMoment(event.getScoreOpponentMoment())
                .createdAt(event.getCreatedAt())
                .build();
    }

    private Athlete resolveAthlete(UUID athleteId, String playerName) {
        if (athleteId != null) {
            java.util.Optional<Athlete> byId = athleteRepository.findById(athleteId);
            if (byId.isPresent()) return byId.get();
        }
        if (playerName != null && !playerName.trim().isEmpty()) {
            String clean = playerName.trim().toLowerCase();
            return athleteRepository.findAll().stream()
                    .filter(a -> a.getName().toLowerCase().equals(clean) || (a.getNickname() != null && a.getNickname().toLowerCase().equals(clean)))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}

