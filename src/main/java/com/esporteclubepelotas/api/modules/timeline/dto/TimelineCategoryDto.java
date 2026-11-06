package com.esporteclubepelotas.api.modules.timeline.dto;

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
public class TimelineCategoryDto {
    private UUID id;
    private String name;
    private String slug;
    private String icon;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
    private Long eventsCount;
    private Instant createdAt;
    private Instant updatedAt;
}

