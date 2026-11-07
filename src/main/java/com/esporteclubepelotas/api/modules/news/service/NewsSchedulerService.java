package com.esporteclubepelotas.api.modules.news.service;

import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.news.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsSchedulerService {

    private final NewsArticleRepository articleRepository;
    private final PortalWebSocketHandler webSocketHandler;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void handleScheduledNewsPublishing() {
        Instant now = Instant.now();
        try {
            int publishedCount = articleRepository.publishScheduledArticles(now);
            if (publishedCount > 0) {
                log.info("[NewsScheduler] Publicadas {} matéria(s) agendada(s) automaticamente.", publishedCount);
                webSocketHandler.broadcastAfterCommit("news", "SCHEDULED_PUBLISHED", null, null);
            }
        } catch (Exception ex) {
            log.error("[NewsScheduler] Erro ao reconciliar matérias agendadas: {}", ex.getMessage(), ex);
        }
    }
}
