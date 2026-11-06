package com.esporteclubepelotas.api.modules.timeline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateTimelineEventDto {

    @NotNull(message = "O ano do marco é obrigatório")
    private Integer year;

    @NotBlank(message = "O título do marco é obrigatório")
    private String title;

    @NotBlank(message = "A descrição do marco é obrigatória")
    private String description;

    private String category;

    private UUID categoryId;

    private LocalDate eventDate;

    private String imageUrl;

    private String dateFormatted;

    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private Boolean isHighlighted = false;

    @Builder.Default
    private Boolean isActive = true;
}
