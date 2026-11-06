package com.esporteclubepelotas.api.modules.timeline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTimelineEventDto {

    private Integer year;
    private String title;
    private String description;
    private String category;
    private UUID categoryId;
    private LocalDate eventDate;
    private String imageUrl;
    private String dateFormatted;
    private Integer displayOrder;
    private Boolean isHighlighted;
    private Boolean isActive;
}
