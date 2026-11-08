package com.esporteclubepelotas.api.modules.sponsor.dto;

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
    private String websiteUrl;
    private Boolean isFeatured;
    private Boolean isActive;
    private Integer displayOrder;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
}

