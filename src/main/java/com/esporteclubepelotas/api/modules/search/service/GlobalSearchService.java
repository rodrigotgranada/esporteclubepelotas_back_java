package com.esporteclubepelotas.api.modules.search.service;

import com.esporteclubepelotas.api.modules.football.entity.Athlete;
import com.esporteclubepelotas.api.modules.football.entity.AthleteType;
import com.esporteclubepelotas.api.modules.football.repository.AthleteRepository;
import com.esporteclubepelotas.api.modules.idol.entity.Idol;
import com.esporteclubepelotas.api.modules.idol.entity.IdolCategory;
import com.esporteclubepelotas.api.modules.idol.repository.IdolRepository;
import com.esporteclubepelotas.api.modules.match.entity.Match;
import com.esporteclubepelotas.api.modules.match.entity.MatchStatus;
import com.esporteclubepelotas.api.modules.match.repository.MatchRepository;
import com.esporteclubepelotas.api.modules.news.entity.NewsArticle;
import com.esporteclubepelotas.api.modules.news.repository.NewsArticleRepository;
import com.esporteclubepelotas.api.modules.search.dto.GlobalSearchItemDto;
import com.esporteclubepelotas.api.modules.search.dto.GlobalSearchResponseDto;
import com.esporteclubepelotas.api.modules.systemmodule.entity.SystemModule;
import com.esporteclubepelotas.api.modules.systemmodule.service.SystemModuleService;
import com.esporteclubepelotas.api.modules.transparency.entity.TransparencyDocument;
import com.esporteclubepelotas.api.modules.transparency.repository.TransparencyDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GlobalSearchService {

    private static final int MIN_QUERY_LENGTH = 2;
    private static final int MAX_QUERY_LENGTH = 80;
    private static final DateTimeFormatter MATCH_DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("America/Sao_Paulo"));

    private final NewsArticleRepository newsArticleRepository;
    private final AthleteRepository athleteRepository;
    private final IdolRepository idolRepository;
    private final MatchRepository matchRepository;
    private final TransparencyDocumentRepository transparencyDocumentRepository;
    private final SystemModuleService systemModuleService;

    @Transactional(readOnly = true)
    public GlobalSearchResponseDto search(String rawQuery, int limit) {
        String normalized = rawQuery == null ? "" : rawQuery.trim();
        if (normalized.length() > MAX_QUERY_LENGTH) {
            normalized = normalized.substring(0, MAX_QUERY_LENGTH).trim();
        }

        if (normalized.length() < MIN_QUERY_LENGTH) {
            return GlobalSearchResponseDto.builder()
                    .query(normalized)
                    .totalResults(0)
                    .news(Collections.emptyList())
                    .athletes(Collections.emptyList())
                    .idols(Collections.emptyList())
                    .matches(Collections.emptyList())
                    .transparency(Collections.emptyList())
                    .build();
        }

        int safeLimit = Math.max(1, Math.min(limit, 10));
        PageRequest pageable = PageRequest.of(0, safeLimit);
        String likePattern = buildSafeLikePattern(normalized);

        Map<String, Boolean> moduleStates = systemModuleService.findAll().stream()
                .collect(Collectors.toMap(SystemModule::getSlug, SystemModule::isActive, (a, b) -> b));

        boolean newsActive = moduleStates.getOrDefault("news", true);
        boolean footballActive = moduleStates.getOrDefault("football", true);
        boolean youthActive = moduleStates.getOrDefault("youth", true);
        boolean femaleActive = moduleStates.getOrDefault("female", true);
        boolean idolsActive = moduleStates.getOrDefault("idols", true);
        boolean matchesActive = moduleStates.getOrDefault("matches", true);
        boolean transparencyActive = moduleStates.getOrDefault("transparency", true);

        List<GlobalSearchItemDto> news = newsActive
                ? newsArticleRepository.searchPublicArticles(Instant.now(), likePattern, youthActive, femaleActive, pageable)
                .stream().map(this::mapNews).toList()
                : Collections.emptyList();

        List<GlobalSearchItemDto> athletes = (footballActive || youthActive || femaleActive)
                ? athleteRepository.searchPublicAthletes(likePattern, footballActive, youthActive, femaleActive, pageable)
                .stream().map(this::mapAthlete).toList()
                : Collections.emptyList();

        List<GlobalSearchItemDto> idols = idolsActive
                ? idolRepository.searchPublicIdols(likePattern, pageable)
                .stream().map(this::mapIdol).toList()
                : Collections.emptyList();

        List<GlobalSearchItemDto> matches = matchesActive
                ? matchRepository.searchPublicMatches(likePattern, pageable)
                .stream().map(this::mapMatch).toList()
                : Collections.emptyList();

        List<GlobalSearchItemDto> transparency = transparencyActive
                ? transparencyDocumentRepository.searchPublished(likePattern, pageable)
                .stream().map(this::mapTransparency).toList()
                : Collections.emptyList();

        int total = news.size() + athletes.size() + idols.size() + matches.size() + transparency.size();

        return GlobalSearchResponseDto.builder()
                .query(normalized)
                .totalResults(total)
                .news(news)
                .athletes(athletes)
                .idols(idols)
                .matches(matches)
                .transparency(transparency)
                .build();
    }

    private String buildSafeLikePattern(String query) {
        String escaped = query.toLowerCase(Locale.ROOT)
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return "%" + escaped + "%";
    }

    private GlobalSearchItemDto mapNews(NewsArticle article) {
        String categoryName = article.getCategory() != null ? article.getCategory().getName() : "Notícia";
        return GlobalSearchItemDto.builder()
                .id(article.getId().toString())
                .slug(article.getSlug())
                .type("NEWS")
                .title(article.getTitle())
                .subtitle(article.getSubtitle() != null && !article.getSubtitle().isBlank()
                        ? article.getSubtitle()
                        : article.getSummary())
                .badge(categoryName)
                .imageUrl(article.getCoverImage())
                .url("/noticias/" + article.getSlug())
                .build();
    }

    private GlobalSearchItemDto mapAthlete(Athlete athlete) {
        String displayName = (athlete.getNickname() != null && !athlete.getNickname().isBlank())
                ? athlete.getNickname() + " (" + athlete.getName() + ")"
                : athlete.getName();

        String badge = athlete.getAthleteType() == AthleteType.STAFF
                ? (athlete.getStaffRole() != null && !athlete.getStaffRole().isBlank() ? athlete.getStaffRole() : "Comissão")
                : athlete.getPrimaryPosition();

        String subtitle = athlete.getAthleteType() == AthleteType.YOUTH
                ? "Categorias de Base"
                : (athlete.getGender() != null && "FEMALE".equals(athlete.getGender().name())
                        ? "Futebol Feminino"
                        : "Futebol Profissional");

        return GlobalSearchItemDto.builder()
                .id(athlete.getId().toString())
                .slug(athlete.getSlug())
                .type("ATHLETE")
                .title(displayName)
                .subtitle(subtitle)
                .badge(badge)
                .imageUrl(athlete.getPhotoUrl())
                .url("/elenco/" + athlete.getSlug())
                .build();
    }

    private GlobalSearchItemDto mapIdol(Idol idol) {
        String displayName = (idol.getNickname() != null && !idol.getNickname().isBlank())
                ? idol.getNickname() + " • " + idol.getName()
                : idol.getName();

        String categoryLabel = translateIdolCategory(idol.getCategory());
        String period = idol.getEndYear() != null
                ? idol.getStartYear() + " - " + idol.getEndYear()
                : String.valueOf(idol.getStartYear());

        return GlobalSearchItemDto.builder()
                .id(idol.getId())
                .slug(idol.getSlug())
                .type("IDOL")
                .title(displayName)
                .subtitle("Período: " + period)
                .badge(categoryLabel)
                .imageUrl(idol.getPhotoUrl())
                .url("/idolos/" + idol.getSlug())
                .build();
    }

    private GlobalSearchItemDto mapMatch(Match match) {
        String opponentName = (match.getOpponentClub() != null && match.getOpponentClub().getName() != null)
                ? match.getOpponentClub().getName()
                : match.getOpponentName();

        String badgeUrl = (match.getOpponentClub() != null && match.getOpponentClub().getBadgeUrl() != null)
                ? match.getOpponentClub().getBadgeUrl()
                : match.getOpponentLogoUrl();

        String title = match.getStatus() == MatchStatus.FINISHED && match.getPelotasScore() != null && match.getOpponentScore() != null
                ? String.format("Pelotas %d x %d %s", match.getPelotasScore(), match.getOpponentScore(), opponentName)
                : String.format("Pelotas x %s", opponentName);

        String championshipName = match.getChampionship() != null ? match.getChampionship().getName() : "Campeonato";
        String dateText = match.getMatchDate() != null ? MATCH_DATE_FMT.format(match.getMatchDate()) : "";
        String subtitle = championshipName + " • " + match.getRoundName() + (dateText.isEmpty() ? "" : " • " + dateText);

        String statusBadge = translateMatchStatus(match.getStatus());
        String matchUrl = Boolean.TRUE.equals(match.getHasLiveCoverage())
                ? "/jogos/" + (match.getSlug() != null && !match.getSlug().isBlank() ? match.getSlug() : match.getId()) + "/ao-vivo"
                : "/jogos";

        return GlobalSearchItemDto.builder()
                .id(match.getId().toString())
                .slug(match.getSlug())
                .type("MATCH")
                .title(title)
                .subtitle(subtitle)
                .badge(statusBadge)
                .imageUrl(badgeUrl)
                .url(matchUrl)
                .build();
    }

    private GlobalSearchItemDto mapTransparency(TransparencyDocument doc) {
        return GlobalSearchItemDto.builder()
                .id(doc.getId().toString())
                .slug(doc.getId().toString())
                .type("TRANSPARENCY")
                .title(doc.getTitle())
                .subtitle("Exercício " + doc.getYear() + (doc.getFileSize() != null ? " • " + doc.getFileSize() : ""))
                .badge(doc.getCategory())
                .url("/transparencia")
                .externalUrl(doc.getFileUrl())
                .build();
    }

    private String translateIdolCategory(IdolCategory category) {
        if (category == null) return "Ídolo";
        return switch (category) {
            case PLAYER -> "Jogador";
            case COACH -> "Treinador";
            case STAFF -> "Colaborador";
            case FAN -> "Torcedor";
        };
    }

    private String translateMatchStatus(MatchStatus status) {
        if (status == null) return "Partida";
        return switch (status) {
            case SCHEDULED -> "Agendado";
            case IN_PROGRESS -> "Ao Vivo";
            case FINISHED -> "Encerrado";
            case POSTPONED -> "Adiado";
        };
    }
}
