package com.esporteclubepelotas.api.modules.timeline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
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
    private UUID categoryId;
    private String category;
    private String categoryName;
    private String categoryIcon;
    private String imageUrl;
    private LocalDate eventDate;
    private String dateFormatted;
    private Integer displayOrder;
    private Boolean isHighlighted;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
