package com.esporteclubepelotas.api.modules.stadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StadiumFactDto {
    private String year;
    private String title;
    private String description;
}

