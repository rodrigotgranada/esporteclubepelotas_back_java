package com.esporteclubepelotas.api.modules.timeline.helper;

import com.esporteclubepelotas.api.modules.timeline.dto.TimelineEventDto;
import com.esporteclubepelotas.api.modules.timeline.entity.TimelineEvent;

public final class TimelineMapperHelper {

    private TimelineMapperHelper() {
    }

    public static TimelineEventDto map(TimelineEvent event) {
        if (event == null) return null;
        return TimelineEventDto.builder()
                .id(event.getId())
                ._id(event.getId() != null ? event.getId().toString() : null)
                .year(event.getYear())
                .title(event.getTitle())
                .description(event.getDescription())
                .category(event.getCategory())
                .imageUrl(event.getImageUrl())
                .dateFormatted(event.getDateFormatted())
                .displayOrder(event.getDisplayOrder())
                .isHighlighted(event.isHighlighted())
                .isActive(event.isActive())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
