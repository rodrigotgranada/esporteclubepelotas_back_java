package com.esporteclubepelotas.api.modules.kit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubKitCompetitionItemResponse {
    private String competition;
    private Integer seasonYear;
    private String label; // Ex: "Copa FGF 2025" or "Gauchão Série A2 2024"
}

