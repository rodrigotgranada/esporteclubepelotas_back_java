package com.esporteclubepelotas.api.modules.newsletter.service;

import com.esporteclubepelotas.api.common.email.EmailService;
import com.esporteclubepelotas.api.common.email.EmailTemplateHelper;
import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.modules.news.entity.NewsArticle;
import com.esporteclubepelotas.api.modules.newsletter.dto.NewsletterSubscriberDto;
import com.esporteclubepelotas.api.modules.newsletter.dto.SubscribeNewsletterDto;
import com.esporteclubepelotas.api.modules.newsletter.entity.NewsletterSubscriber;
import com.esporteclubepelotas.api.modules.newsletter.repository.NewsletterSubscriberRepository;
import com.esporteclubepelotas.api.modules.setting.service.SettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class NewsletterService {

    private final NewsletterSubscriberRepository subscriberRepository;
    private final SettingService settingService;
    private final EmailService emailService;
    private final Executor taskExecutor;

    @Value("${frontend.url:https://esporte-clube-pelotas-dev.vercel.app}")
    private String frontendUrl;

    public NewsletterService(
            NewsletterSubscriberRepository subscriberRepository,
            SettingService settingService,
            EmailService emailService,
            @Qualifier("taskExecutor") Executor taskExecutor
    ) {
        this.subscriberRepository = subscriberRepository;
        this.settingService = settingService;
        this.emailService = emailService;
        this.taskExecutor = taskExecutor;
    }

    @Transactional
    public NewsletterSubscriberDto subscribe(SubscribeNewsletterDto dto) {
        var settings = settingService.getSettings();
        if (settings.getNewsletterIsActive() != null && !settings.getNewsletterIsActive()) {
            throw new BusinessException("A captação de novos inscritos na Newsletter está temporariamente suspensa pelo clube.");
        }

        String email = dto.getEmail().trim().toLowerCase();
        var existingOpt = subscriberRepository.findByEmailIgnoreCase(email);

        NewsletterSubscriber subscriber;
        boolean shouldSendWelcome = false;

        if (existingOpt.isPresent()) {
            subscriber = existingOpt.get();
            if (!subscriber.isActive()) {
                subscriber.setActive(true);
                subscriber.setUnsubscribeReason(null);
                subscriber.setUnsubscribedAt(null);
                shouldSendWelcome = true;
            }
            if (subscriber.getUnsubscribeToken() == null || subscriber.getUnsubscribeToken().isBlank()) {
                subscriber.setUnsubscribeToken(UUID.randomUUID().toString());
            }
            if (dto.getName() != null && !dto.getName().isBlank()) {
                subscriber.setName(dto.getName().trim());
            }
        } else {
            subscriber = NewsletterSubscriber.builder()
                    .email(email)
                    .name(dto.getName() != null && !dto.getName().isBlank() ? dto.getName().trim() : null)
                    .active(true)
                    .unsubscribeToken(UUID.randomUUID().toString())
                    .build();
            shouldSendWelcome = true;
        }

        subscriber = subscriberRepository.save(subscriber);

        if (shouldSendWelcome) {
            final String targetEmail = subscriber.getEmail();
            final String targetName = subscriber.getName();
            final String unsubscribeUrl = buildUnsubscribeUrl(subscriber.getUnsubscribeToken());
            runAfterCommit(() -> taskExecutor.execute(() -> {
                String html = EmailTemplateHelper.getNewsletterWelcomeEmail(targetName, frontendUrl, unsubscribeUrl);
                emailService.sendHtmlEmail(targetEmail, "Bem-vindo à Newsletter Oficial - E.C. Pelotas", html);
            }));
        }

        return toDto(subscriber);
    }

    @Transactional
    public void unsubscribeByToken(String token, String reason) {
        if (token == null || token.isBlank()) {
            throw new BusinessException("Token de descadastro inválido.");
        }
        NewsletterSubscriber subscriber = subscriberRepository.findByUnsubscribeToken(token.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Inscrição da newsletter não encontrada ou token inválido."));
        subscriber.setActive(false);
        subscriber.setUnsubscribedAt(Instant.now());
        subscriber.setUnsubscribeReason(reason != null && !reason.isBlank() ? reason.trim() : null);
        subscriberRepository.save(subscriber);
        log.info("📧 [Newsletter] Inscrito {} desativado via token de descadastro (motivo={}).",
                subscriber.getEmail(), subscriber.getUnsubscribeReason());
    }

    public void dispatchArticleAfterCommit(NewsArticle article) {
        if (article == null) return;

        final String title = article.getTitle();
        final String subtitle = article.getSubtitle();
        final String summary = article.getSummary();
        final String categoryName = article.getCategory() != null ? article.getCategory().getName() : null;
        final String coverImage = article.getCoverImage();
        final String slug = article.getSlug();

        runAfterCommit(() -> taskExecutor.execute(() ->
                dispatchArticleToActiveSubscribers(title, subtitle, summary, categoryName, coverImage, slug)
        ));
    }

    private void dispatchArticleToActiveSubscribers(
            String title,
            String subtitle,
            String summary,
            String categoryName,
            String coverImage,
            String slug
    ) {
        List<NewsletterSubscriber> activeSubscribers = subscriberRepository.findByActiveTrue();
        if (activeSubscribers.isEmpty()) {
            log.info("📧 [Newsletter] Nenhum inscrito ativo para envio da matéria '{}'.", title);
            return;
        }

        String subject = "📰 E.C. Pelotas: " + title;
        log.info("📧 [Newsletter] Iniciando disparo da matéria '{}' para {} inscrito(s) ativo(s).", title, activeSubscribers.size());

        for (int i = 0; i < activeSubscribers.size(); i++) {
            NewsletterSubscriber sub = activeSubscribers.get(i);
            try {
                String unsubscribeUrl = buildUnsubscribeUrl(sub.getUnsubscribeToken());
                String html = EmailTemplateHelper.getNewsNewsletterEmail(
                        title,
                        subtitle,
                        summary,
                        categoryName,
                        coverImage,
                        slug,
                        frontendUrl,
                        unsubscribeUrl
                );
                emailService.sendHtmlEmail(sub.getEmail(), subject, html);

                if (activeSubscribers.size() > 1 && i < activeSubscribers.size() - 1) {
                    Thread.sleep(550);
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.warn("⚠️ [Newsletter] Disparo interrompido durante envio da matéria '{}'.", title);
                return;
            } catch (Exception ex) {
                log.error("❌ [Newsletter] Erro ao enfileirar e-mail para {}: {}", sub.getEmail(), ex.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<NewsletterSubscriberDto> listSubscribers(int page, int limit) {
        int pageIndex = Math.max(0, page - 1);
        int pageSize = Math.max(1, limit);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return subscriberRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<NewsletterSubscriberDto> listAllSubscribers() {
        return subscriberRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public void deleteSubscriber(UUID id) {
        NewsletterSubscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscrição de newsletter não encontrada: " + id));
        subscriberRepository.delete(subscriber);
    }

    private String buildUnsubscribeUrl(String token) {
        String baseUrl = (frontendUrl != null && !frontendUrl.isBlank() ? frontendUrl : "http://localhost:7999")
                .replaceAll("/+$", "");
        return baseUrl + "/newsletter/cancelar?token=" + token;
    }

    private void runAfterCommit(Runnable action) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    action.run();
                }
            });
        } else {
            action.run();
        }
    }

    private NewsletterSubscriberDto toDto(NewsletterSubscriber s) {
        return NewsletterSubscriberDto.builder()
                .id(s.getId())
                .email(s.getEmail())
                .name(s.getName())
                .active(s.isActive())
                .unsubscribeReason(s.getUnsubscribeReason())
                .unsubscribedAt(s.getUnsubscribedAt())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
