package com.esporteclubepelotas.api.modules.timeline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTimelineCategoryDto {

    private String name;
    private String slug;
    private String icon;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
}

