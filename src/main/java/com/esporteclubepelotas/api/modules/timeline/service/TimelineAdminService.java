package com.esporteclubepelotas.api.modules.timeline.service;

import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.modules.timeline.dto.*;
import com.esporteclubepelotas.api.modules.timeline.entity.TimelineCategory;
import com.esporteclubepelotas.api.modules.timeline.entity.TimelineEvent;
import com.esporteclubepelotas.api.modules.timeline.helper.TimelineMapperHelper;
import com.esporteclubepelotas.api.modules.timeline.repository.TimelineEventRepository;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineAdminService {

    private final TimelineEventRepository timelineEventRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final TimelineCategoryService timelineCategoryService;

    private static final DateTimeFormatter PT_BR_DATE_FORMATTER = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));

    @Transactional(readOnly = true)
    public GetTimelineResponseDto findAll(QueryTimelineDto query) {
        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int limit = query.getLimit() != null && query.getLimit() > 0 ? Math.min(200, query.getLimit()) : 50;

        Specification<TimelineEvent> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), query.getIsActive()));
            }

            if (query.getYear() != null) {
                predicates.add(cb.equal(root.get("year"), query.getYear()));
            }

            if (query.getCategory() != null && !query.getCategory().isBlank()) {
                String catParam = query.getCategory().trim().toLowerCase();
                try {
                    UUID catUuid = UUID.fromString(catParam);
                    predicates.add(cb.equal(root.get("category").get("id"), catUuid));
                } catch (IllegalArgumentException e) {
                    predicates.add(cb.or(
                            cb.equal(cb.lower(root.get("category").get("slug")), catParam),
                            cb.equal(cb.lower(cb.coalesce(root.get("categoryLegacy"), "")), catParam)
                    ));
                }
            }

            if (query.getIsHighlighted() != null) {
                predicates.add(cb.equal(root.get("isHighlighted"), query.getIsHighlighted()));
            }

            if (query.getSearch() != null && !query.getSearch().isBlank()) {
                String pattern = "%" + query.getSearch().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(cb.coalesce(root.get("title"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("description"), "")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(
                Sort.Order.asc("year"),
                Sort.Order.asc("displayOrder"),
                Sort.Order.asc("createdAt")
        );

        Page<TimelineEvent> paged = timelineEventRepository.findAll(spec, PageRequest.of(page - 1, limit, sort));

        List<TimelineEventDto> dtos = paged.getContent().stream()
                .map(TimelineMapperHelper::map)
                .toList();

        return GetTimelineResponseDto.builder()
                .data(dtos)
                .total(paged.getTotalElements())
                .page(page)
                .limit(limit)
                .totalPages(paged.getTotalPages() > 0 ? paged.getTotalPages() : 1)
                .build();
    }

    @Transactional(readOnly = true)
    public TimelineEventDto findById(UUID id) {
        TimelineEvent event = timelineEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marco Histórico", "id", id));
        return TimelineMapperHelper.map(event);
    }

    @Transactional
    public TimelineEventDto create(CreateTimelineEventDto dto, MultipartFile photoFile, String userId) {
        User creator = null;
        if (userId != null && !userId.isBlank()) {
            creator = userRepository.findById(userId).orElse(null);
        }

        String imageUrl = dto.getImageUrl();
        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                String originalFilename = photoFile.getOriginalFilename() != null ? photoFile.getOriginalFilename() : "timeline.png";
                imageUrl = storageService.uploadFile(
                        photoFile.getBytes(),
                        "timeline-" + dto.getYear() + "-" + System.currentTimeMillis() + "-" + originalFilename,
                        "timeline",
                        photoFile.getContentType()
                );
            } catch (IOException e) {
                log.error("Erro ao realizar upload de imagem da linha do tempo: {}", e.getMessage(), e);
                throw new RuntimeException("Falha ao salvar imagem histórica", e);
            }
        }

        TimelineCategory resolvedCategory = timelineCategoryService.resolveCategory(dto.getCategory(), dto.getCategoryId());

        String dateFormatted = dto.getDateFormatted() != null && !dto.getDateFormatted().isBlank()
                ? dto.getDateFormatted().trim()
                : (dto.getEventDate() != null ? dto.getEventDate().format(PT_BR_DATE_FORMATTER) : null);

        int year = dto.getYear();
        if (year <= 0 && dto.getEventDate() != null) {
            year = dto.getEventDate().getYear();
        }

        TimelineEvent event = TimelineEvent.builder()
                .year(year)
                .title(dto.getTitle().trim())
                .description(dto.getDescription().trim())
                .category(resolvedCategory)
                .categoryLegacy(resolvedCategory != null ? resolvedCategory.getSlug() : (dto.getCategory() != null ? dto.getCategory().trim() : null))
                .imageUrl(imageUrl)
                .galleryUrls(dto.getGalleryUrls() != null ? dto.getGalleryUrls() : new ArrayList<>())
                .eventDate(dto.getEventDate())
                .dateFormatted(dateFormatted)
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .isHighlighted(Boolean.TRUE.equals(dto.getIsHighlighted()))
                .isActive(dto.getIsActive() == null || dto.getIsActive())
                .createdBy(creator)
                .build();

        TimelineEvent saved = timelineEventRepository.save(event);
        log.info("✅ Marco histórico criado com sucesso: {} - {}", saved.getYear(), saved.getTitle());
        return TimelineMapperHelper.map(saved);
    }

    @Transactional
    public TimelineEventDto update(UUID id, UpdateTimelineEventDto dto, MultipartFile photoFile) {
        TimelineEvent event = timelineEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marco Histórico", "id", id));

        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                String originalFilename = photoFile.getOriginalFilename() != null ? photoFile.getOriginalFilename() : "timeline.png";
                String imageUrl = storageService.uploadFile(
                        photoFile.getBytes(),
                        "timeline-" + event.getYear() + "-" + System.currentTimeMillis() + "-" + originalFilename,
                        "timeline",
                        photoFile.getContentType()
                );
                event.setImageUrl(imageUrl);
            } catch (IOException e) {
                log.error("Erro ao atualizar imagem da linha do tempo: {}", e.getMessage(), e);
                throw new RuntimeException("Falha ao atualizar imagem histórica", e);
            }
        } else if (dto != null && dto.getImageUrl() != null) {
            event.setImageUrl(dto.getImageUrl());
        }

        if (dto != null) {
            if (dto.getYear() != null) event.setYear(dto.getYear());
            if (dto.getTitle() != null && !dto.getTitle().isBlank()) event.setTitle(dto.getTitle().trim());
            if (dto.getDescription() != null && !dto.getDescription().isBlank()) event.setDescription(dto.getDescription().trim());

            if (dto.getCategoryId() != null || (dto.getCategory() != null && !dto.getCategory().isBlank())) {
                TimelineCategory resolved = timelineCategoryService.resolveCategory(dto.getCategory(), dto.getCategoryId());
                if (resolved != null) {
                    event.setCategory(resolved);
                    event.setCategoryLegacy(resolved.getSlug());
                }
            }

            if (dto.getEventDate() != null) {
                event.setEventDate(dto.getEventDate());
                if (dto.getDateFormatted() == null || dto.getDateFormatted().isBlank()) {
                    event.setDateFormatted(dto.getEventDate().format(PT_BR_DATE_FORMATTER));
                }
            }

            if (dto.getDateFormatted() != null) event.setDateFormatted(dto.getDateFormatted().trim());
            if (dto.getGalleryUrls() != null) event.setGalleryUrls(dto.getGalleryUrls());
            if (dto.getDisplayOrder() != null) event.setDisplayOrder(dto.getDisplayOrder());
            if (dto.getIsHighlighted() != null) event.setHighlighted(dto.getIsHighlighted());
            if (dto.getIsActive() != null) event.setActive(dto.getIsActive());
        }

        TimelineEvent saved = timelineEventRepository.save(event);
        log.info("✅ Marco histórico atualizado: {} - {}", saved.getYear(), saved.getTitle());
        return TimelineMapperHelper.map(saved);
    }

    @Transactional
    public void delete(UUID id) {
        TimelineEvent event = timelineEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marco Histórico", "id", id));
        event.setActive(false);
        timelineEventRepository.save(event);
        log.info("🗑️ Marco histórico inativado (soft delete): {}", id);
    }
}
