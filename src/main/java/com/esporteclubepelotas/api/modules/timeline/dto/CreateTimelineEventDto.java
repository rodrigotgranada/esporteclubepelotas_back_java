package com.esporteclubepelotas.api.modules.timeline.dto;

import com.esporteclubepelotas.api.modules.timeline.entity.TimelineCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotNull(message = "A categoria do marco é obrigatória")
    private TimelineCategory category;

    private String imageUrl;

    private String dateFormatted;

    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private Boolean isHighlighted = false;

    @Builder.Default
    private Boolean isActive = true;
}
