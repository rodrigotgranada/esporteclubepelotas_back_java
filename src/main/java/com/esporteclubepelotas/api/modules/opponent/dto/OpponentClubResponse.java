package com.esporteclubepelotas.api.modules.opponent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpponentClubResponse {
    private UUID id;
    private String name;
    private String shortName;
    private String badgeUrl;
    private String city;
    private String stadiumName;
    private Boolean isActive;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;
}

