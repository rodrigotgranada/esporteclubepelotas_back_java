package com.esporteclubepelotas.api.modules.sponsor.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorDto {

    private UUID id;
    private String name;
    private String logoUrl;

    @JsonProperty("logoDarkUrl")
    @JsonAlias({"logo_dark_url"})
    private String logoDarkUrl;

    private String websiteUrl;

    @JsonProperty("isFeatured")
    @JsonAlias({"featured", "is_featured"})
    private boolean isFeatured;

    @JsonProperty("isActive")
    @JsonAlias({"active", "is_active"})
    private boolean isActive;

    private Integer displayOrder;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private Long clicksCount;
    private String status; // ACTIVE, SCHEDULED, EXPIRED, INACTIVE
    private Instant createdAt;
    private Instant updatedAt;
}

