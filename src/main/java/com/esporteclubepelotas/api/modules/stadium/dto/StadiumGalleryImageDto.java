package com.esporteclubepelotas.api.modules.stadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StadiumGalleryImageDto {
    private String url;
    private String caption;
    private Integer order;
}

