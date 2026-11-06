package com.esporteclubepelotas.api.modules.timeline.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.modules.timeline.dto.CreateTimelineCategoryDto;
import com.esporteclubepelotas.api.modules.timeline.dto.TimelineCategoryDto;
import com.esporteclubepelotas.api.modules.timeline.dto.UpdateTimelineCategoryDto;
import com.esporteclubepelotas.api.modules.timeline.entity.TimelineCategory;
import com.esporteclubepelotas.api.modules.timeline.helper.TimelineMapperHelper;
import com.esporteclubepelotas.api.modules.timeline.repository.TimelineCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimelineCategoryService {

    private final TimelineCategoryRepository categoryRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Transactional(readOnly = true)
    public List<TimelineCategoryDto> findAllPublic() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAscNameAsc().stream()
                .map(cat -> TimelineMapperHelper.mapCategory(cat, categoryRepository.countEventsByCategoryId(cat.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimelineCategoryDto> findAllAdmin() {
        return categoryRepository.findAllByOrderByDisplayOrderAscNameAsc().stream()
                .map(cat -> TimelineMapperHelper.mapCategory(cat, categoryRepository.countEventsByCategoryId(cat.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public TimelineCategory findById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria não encontrada com o ID: " + id));
    }

    @Transactional
    public TimelineCategoryDto create(CreateTimelineCategoryDto dto) {
        String slug = dto.getSlug() != null && !dto.getSlug().isBlank()
                ? toSlug(dto.getSlug())
                : toSlug(dto.getName());

        int suffix = 1;
        String finalSlug = slug;
        while (categoryRepository.existsBySlug(finalSlug)) {
            finalSlug = slug + "-" + suffix++;
        }

        TimelineCategory category = TimelineCategory.builder()
                .name(dto.getName().trim())
                .slug(finalSlug)
                .icon(dto.getIcon() != null && !dto.getIcon().isBlank() ? dto.getIcon().trim() : "Flag")
                .description(dto.getDescription() != null ? dto.getDescription().trim() : null)
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();

        TimelineCategory saved = categoryRepository.save(category);
        log.info("Timeline category created: id={}, name={}, slug={}", saved.getId(), saved.getName(), saved.getSlug());
        return TimelineMapperHelper.mapCategory(saved, 0L);
    }

    @Transactional
    public TimelineCategoryDto update(UUID id, UpdateTimelineCategoryDto dto) {
        TimelineCategory category = findById(id);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            category.setName(dto.getName().trim());
        }

        if (dto.getSlug() != null && !dto.getSlug().isBlank()) {
            String newSlug = toSlug(dto.getSlug());
            if (categoryRepository.existsBySlugAndIdNot(newSlug, id)) {
                throw new BusinessException("Já existe outra categoria com este slug: " + newSlug);
            }
            category.setSlug(newSlug);
        }

        if (dto.getIcon() != null && !dto.getIcon().isBlank()) {
            category.setIcon(dto.getIcon().trim());
        }

        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription().trim());
        }

        if (dto.getDisplayOrder() != null) {
            category.setDisplayOrder(dto.getDisplayOrder());
        }

        if (dto.getIsActive() != null) {
            category.setActive(dto.getIsActive());
        }

        TimelineCategory updated = categoryRepository.save(category);
        log.info("Timeline category updated: id={}, name={}", updated.getId(), updated.getName());
        return TimelineMapperHelper.mapCategory(updated, categoryRepository.countEventsByCategoryId(updated.getId()));
    }

    @Transactional
    public void delete(UUID id) {
        TimelineCategory category = findById(id);
        long eventsCount = categoryRepository.countEventsByCategoryId(id);
        if (eventsCount > 0) {
            // Se houver eventos, faz inativação lógica para não quebrar a integridade referencial (Regra 7)
            category.setActive(false);
            categoryRepository.save(category);
            log.info("Timeline category deactivated because it has {} events: id={}", eventsCount, id);
        } else {
            categoryRepository.delete(category);
            log.info("Timeline category deleted: id={}", id);
        }
    }

    public TimelineCategory resolveCategory(String categoryParam, UUID categoryId) {
        if (categoryId != null) {
            return categoryRepository.findById(categoryId).orElse(null);
        }
        if (categoryParam == null || categoryParam.isBlank()) {
            return null;
        }

        String param = categoryParam.trim();
        try {
            UUID parsedUuid = UUID.fromString(param);
            return categoryRepository.findById(parsedUuid).orElse(null);
        } catch (IllegalArgumentException ignored) {
            // Não é UUID, busca por slug
        }

        String slug = toSlug(param);
        return categoryRepository.findBySlug(slug)
                .or(() -> categoryRepository.findBySlug(param.toLowerCase()))
                .orElse(null);
    }

    private static String toSlug(String input) {
        if (input == null) return "";
        String nowhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH).replaceAll("-+", "-").replaceAll("^-|-$", "");
    }
}
