package com.esporteclubepelotas.api.modules.newsletter.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.modules.newsletter.dto.NewsletterSubscriberDto;
import com.esporteclubepelotas.api.modules.newsletter.dto.SubscribeNewsletterDto;
import com.esporteclubepelotas.api.modules.newsletter.entity.NewsletterSubscriber;
import com.esporteclubepelotas.api.modules.newsletter.repository.NewsletterSubscriberRepository;
import com.esporteclubepelotas.api.modules.setting.service.SettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsletterService {

    private final NewsletterSubscriberRepository subscriberRepository;
    private final SettingService settingService;

    @Transactional
    public NewsletterSubscriberDto subscribe(SubscribeNewsletterDto dto) {
        var settings = settingService.getSettings();
        if (settings.getNewsletterIsActive() != null && !settings.getNewsletterIsActive()) {
            throw new BusinessException("A captação de novos inscritos na Newsletter está temporariamente suspensa pelo clube.");
        }

        String email = dto.getEmail().trim().toLowerCase();
        var existingOpt = subscriberRepository.findByEmailIgnoreCase(email);

        NewsletterSubscriber subscriber;
        if (existingOpt.isPresent()) {
            subscriber = existingOpt.get();
            if (!subscriber.isActive()) {
                subscriber.setActive(true);
            }
            if (dto.getName() != null && !dto.getName().isBlank()) {
                subscriber.setName(dto.getName().trim());
            }
        } else {
            subscriber = NewsletterSubscriber.builder()
                    .email(email)
                    .name(dto.getName() != null && !dto.getName().isBlank() ? dto.getName().trim() : null)
                    .active(true)
                    .build();
        }

        subscriber = subscriberRepository.save(subscriber);
        return toDto(subscriber);
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

    private NewsletterSubscriberDto toDto(NewsletterSubscriber s) {
        return NewsletterSubscriberDto.builder()
                .id(s.getId())
                .email(s.getEmail())
                .name(s.getName())
                .active(s.isActive())
                .createdAt(s.getCreatedAt())
                .build();
    }
}

