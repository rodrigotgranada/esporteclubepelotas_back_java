package com.esporteclubepelotas.api.modules.news.service;

import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.news.entity.NewsArticle;
import com.esporteclubepelotas.api.modules.news.repository.NewsArticleRepository;
import com.esporteclubepelotas.api.modules.newsletter.service.NewsletterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsSchedulerService {

    private final NewsArticleRepository articleRepository;
    private final PortalWebSocketHandler webSocketHandler;
    private final NewsletterService newsletterService;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    @CacheEvict(value = "news_featured", allEntries = true)
    public void handleScheduledNewsPublishing() {
        Instant now = Instant.now();
        try {
            List<NewsArticle> dueForNewsletter = articleRepository.findDueScheduledNewsletterArticles(now);
            if (!dueForNewsletter.isEmpty()) {
                for (NewsArticle article : dueForNewsletter) {
                    article.setNewsletterSentAt(now);
                }
                articleRepository.saveAll(dueForNewsletter);
            }

            int publishedCount = articleRepository.publishScheduledArticles(now);
            if (publishedCount > 0) {
                log.info("[NewsScheduler] Publicadas {} matéria(s) agendada(s) automaticamente.", publishedCount);
                webSocketHandler.broadcastAfterCommit("news", "SCHEDULED_PUBLISHED", null, null);
                for (NewsArticle article : dueForNewsletter) {
                    newsletterService.dispatchArticleAfterCommit(article);
                }
            }
        } catch (Exception ex) {
            log.error("[NewsScheduler] Erro ao reconciliar matérias agendadas: {}", ex.getMessage(), ex);
        }
    }
}
