package com.esporteclubepelotas.api.modules.sponsor.dto;

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
    private String websiteUrl;
    private boolean isFeatured;
    private boolean isActive;
    private Integer displayOrder;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private Long clicksCount;
    private String status; // ACTIVE, SCHEDULED, EXPIRED, INACTIVE
    private Instant createdAt;
    private Instant updatedAt;
}
