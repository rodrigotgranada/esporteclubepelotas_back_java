package com.esporteclubepelotas.api.modules.news.helper;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.modules.news.dto.NewsArticleDto;
import com.esporteclubepelotas.api.modules.news.entity.NewsArticle;
import com.esporteclubepelotas.api.modules.news.entity.NewsFeaturedSlot;
import com.esporteclubepelotas.api.modules.news.entity.NewsStatus;
import com.esporteclubepelotas.api.modules.news.repository.NewsArticleRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class NewsScheduledHelper {

    private NewsScheduledHelper() {}

    public static Map<String, Object> getScheduledArticles(NewsArticleRepository repository) {
        Instant now = Instant.now();
        List<NewsArticle> list = repository.findByIsActiveTrueAndStatusAndPublishedAtGreaterThanOrderByPublishedAtAsc(
                NewsStatus.SCHEDULED,
                now
        );

        List<NewsArticleDto> mapped = list.stream()
                .map(NewsMapperHelper::mapArticle)
                .toList();

        return Map.of(
                "data", mapped,
                "total", mapped.size()
        );
    }

    public static NewsArticleDto publishNow(NewsArticleRepository repository, UUID id) {
        NewsArticle article = repository.findById(id)
                .filter(NewsArticle::isActive)
                .orElseThrow(() -> new BusinessException("Matéria agendada não encontrada."));

        if (article.getFeaturedSlot() == NewsFeaturedSlot.MAIN) {
            repository.clearOtherMainSlots(article.getId());
        }

        article.setStatus(NewsStatus.PUBLISHED);
        article.setPublishedAt(Instant.now());

        NewsArticle saved = repository.save(article);
        return NewsMapperHelper.mapArticle(saved);
    }
}

