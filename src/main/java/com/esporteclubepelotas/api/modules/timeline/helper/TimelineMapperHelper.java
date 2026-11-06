package com.esporteclubepelotas.api.modules.timeline.helper;

import com.esporteclubepelotas.api.modules.timeline.dto.TimelineCategoryDto;
import com.esporteclubepelotas.api.modules.timeline.dto.TimelineEventDto;
import com.esporteclubepelotas.api.modules.timeline.entity.TimelineCategory;
import com.esporteclubepelotas.api.modules.timeline.entity.TimelineEvent;

public final class TimelineMapperHelper {

    private TimelineMapperHelper() {
    }

    public static TimelineCategoryDto mapCategory(TimelineCategory category, Long eventsCount) {
        if (category == null) return null;
        return TimelineCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .icon(category.getIcon())
                .description(category.getDescription())
                .displayOrder(category.getDisplayOrder())
                .isActive(category.isActive())
                .eventsCount(eventsCount != null ? eventsCount : 0L)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public static TimelineEventDto map(TimelineEvent event) {
        if (event == null) return null;

        TimelineCategory cat = event.getCategory();
        String catSlug = cat != null ? cat.getSlug() : (event.getCategoryLegacy() != null ? event.getCategoryLegacy().toLowerCase() : "marcos");
        String catName = cat != null ? cat.getName() : (event.getCategoryLegacy() != null ? event.getCategoryLegacy() : "Marco Histórico");
        String catIcon = cat != null ? cat.getIcon() : "Flag";

        return TimelineEventDto.builder()
                .id(event.getId())
                ._id(event.getId() != null ? event.getId().toString() : null)
                .year(event.getYear())
                .title(event.getTitle())
                .description(event.getDescription())
                .categoryId(cat != null ? cat.getId() : null)
                .category(catSlug)
                .categoryName(catName)
                .categoryIcon(catIcon)
                .imageUrl(event.getImageUrl())
                .eventDate(event.getEventDate())
                .dateFormatted(event.getDateFormatted())
                .displayOrder(event.getDisplayOrder())
                .isHighlighted(event.isHighlighted())
                .isActive(event.isActive())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
