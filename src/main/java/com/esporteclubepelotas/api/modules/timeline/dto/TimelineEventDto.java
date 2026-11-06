package com.esporteclubepelotas.api.modules.timeline.dto;

import com.esporteclubepelotas.api.modules.timeline.entity.TimelineCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineEventDto {
    private UUID id;
    private String _id;
    private Integer year;
    private String title;
    private String description;
    private TimelineCategory category;
    private String imageUrl;
    private String dateFormatted;
    private Integer displayOrder;
    private Boolean isHighlighted;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
