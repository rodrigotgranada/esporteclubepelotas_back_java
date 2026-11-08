package com.esporteclubepelotas.api.modules.stadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StadiumQuickFactDto {
    private String id;
    private String label;
    private String value;
    private String icon;
    private Integer order;
}
