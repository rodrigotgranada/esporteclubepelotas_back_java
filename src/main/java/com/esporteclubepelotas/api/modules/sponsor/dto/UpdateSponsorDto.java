package com.esporteclubepelotas.api.modules.sponsor.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSponsorDto {

    private String name;
    private String logoUrl;

    @JsonProperty("logoDarkUrl")
    @JsonAlias({"logo_dark_url"})
    private String logoDarkUrl;

    private String websiteUrl;

    @JsonProperty("isFeatured")
    @JsonAlias({"featured", "is_featured"})
    private Boolean isFeatured;

    @JsonProperty("isActive")
    @JsonAlias({"active", "is_active"})
    private Boolean isActive;

    private Integer displayOrder;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
}

