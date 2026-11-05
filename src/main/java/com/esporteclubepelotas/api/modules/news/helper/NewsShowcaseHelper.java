package com.esporteclubepelotas.api.modules.news.helper;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.modules.news.dto.NewsArticleDto;
import com.esporteclubepelotas.api.modules.news.dto.ShowcaseResponseDto;
import com.esporteclubepelotas.api.modules.news.dto.UpdateShowcaseDto;
import com.esporteclubepelotas.api.modules.news.entity.NewsArticle;
import com.esporteclubepelotas.api.modules.news.entity.NewsFeaturedSlot;
import com.esporteclubepelotas.api.modules.news.repository.NewsArticleRepository;
import org.springframework.data.domain.PageRequest;

import java.util.*;

public final class NewsShowcaseHelper {

    private NewsShowcaseHelper() {}

    public static ShowcaseResponseDto getShowcase(NewsArticleRepository repository) {
        Optional<NewsArticle> mainArticleOpt = repository.findCurrentPublishedMain();
        NewsArticleDto mainDto = mainArticleOpt.map(NewsMapperHelper::mapArticle).orElse(null);

        List<NewsArticle> secondaryList = repository.findPublishedSecondary(PageRequest.of(0, 2));
        List<NewsArticleDto> secondaryDtos = secondaryList.stream()
                .map(NewsMapperHelper::mapArticle)
                .toList();

        Set<UUID> excludedIds = new HashSet<>();
        mainArticleOpt.ifPresent(a -> excludedIds.add(a.getId()));
        secondaryList.forEach(a -> excludedIds.add(a.getId()));

        if (excludedIds.isEmpty()) {
            excludedIds.add(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        }

        List<NewsArticle> availableList = repository.findPublishedAvailable(excludedIds, PageRequest.of(0, 30));
        List<NewsArticleDto> availableDtos = availableList.stream()
                .map(NewsMapperHelper::mapArticle)
                .toList();

        return ShowcaseResponseDto.builder()
                .main(mainDto)
                .secondary(secondaryDtos)
                .available(availableDtos)
                .build();
    }

    public static void updateShowcase(NewsArticleRepository repository, UpdateShowcaseDto dto) {
        List<String> rawSecondaryIds = dto.getSecondaryIds() != null
                ? dto.getSecondaryIds().stream().filter(s -> s != null && !s.isBlank()).toList()
                : Collections.emptyList();

        String mainIdStr = dto.getMainId();

        if (rawSecondaryIds.size() > 2) {
            throw new BusinessException("A vitrine suporta no máximo 2 destaques secundários.");
        }

        if (mainIdStr != null && rawSecondaryIds.contains(mainIdStr)) {
            throw new BusinessException("Uma matéria não pode ser Manchetão e Destaque Secundário ao mesmo tempo.");
        }

        UUID mainId = null;
        if (mainIdStr != null && !mainIdStr.isBlank()) {
            try {
                mainId = UUID.fromString(mainIdStr);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("ID do Manchetão Principal inválido: " + mainIdStr);
            }
        }

        List<UUID> secondaryUuids = new ArrayList<>();
        for (String sId : rawSecondaryIds) {
            try {
                secondaryUuids.add(UUID.fromString(sId));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("ID de destaque secundário inválido: " + sId);
            }
        }

        Set<UUID> keepIds = new HashSet<>();
        if (mainId != null) keepIds.add(mainId);
        keepIds.addAll(secondaryUuids);

        if (keepIds.isEmpty()) {
            repository.clearAllMainSlots();
            repository.clearSlotsExcept(Collections.singleton(UUID.fromString("00000000-0000-0000-0000-000000000000")));
            return;
        }

        repository.clearSlotsExcept(keepIds);

        if (mainId != null) {
            repository.findById(mainId).ifPresent(article -> {
                article.setFeaturedSlot(NewsFeaturedSlot.MAIN);
                article.setFeatured(true);
                repository.save(article);
            });
        }

        if (!secondaryUuids.isEmpty()) {
            repository.setSecondarySlots(secondaryUuids);
        }
    }

    public static NewsArticleDto getCurrentMain(NewsArticleRepository repository) {
        return repository.findCurrentPublishedMain()
                .map(NewsMapperHelper::mapArticle)
                .orElse(null);
    }
}

