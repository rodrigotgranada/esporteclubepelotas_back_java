package com.esporteclubepelotas.api.modules.sponsor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuerySponsorDto {

    private String search;
    private Boolean isFeatured;
    private Boolean isActive;
    private String status; // ACTIVE, SCHEDULED, EXPIRED, INACTIVE
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer limit = 20;
}

