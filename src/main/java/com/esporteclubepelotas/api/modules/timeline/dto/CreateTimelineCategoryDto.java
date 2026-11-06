package com.esporteclubepelotas.api.modules.timeline.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTimelineCategoryDto {

    @NotBlank(message = "O nome da categoria é obrigatório")
    private String name;

    private String slug;

    @Builder.Default
    private String icon = "Flag";

    private String description;

    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private Boolean isActive = true;
}
