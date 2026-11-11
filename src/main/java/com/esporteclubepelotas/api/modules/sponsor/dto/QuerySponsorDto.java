package com.esporteclubepelotas.api.modules.sponsor.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("isFeatured")
    @JsonAlias({"featured", "is_featured"})
    private Boolean isFeatured;

    @JsonProperty("isActive")
    @JsonAlias({"active", "is_active"})
    private Boolean isActive;

    private String status; // ACTIVE, SCHEDULED, EXPIRED, INACTIVE
    private String scope; // ALL, MAIN, YOUTH, FEMALE
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer limit = 20;
}

