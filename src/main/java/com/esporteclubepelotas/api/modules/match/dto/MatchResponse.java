package com.esporteclubepelotas.api.modules.match.dto;

import com.esporteclubepelotas.api.modules.match.entity.HomeAway;
import com.esporteclubepelotas.api.modules.match.entity.MatchStatus;
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
public class MatchResponse {
    private UUID id;
    private UUID championshipId;
    private String championshipName;
    private Integer seasonYear;
    private String opponentName;
    private String opponentLogoUrl;
    private HomeAway homeAway;
    private String stadiumName;
    private String city;
    private Instant matchDate;
    private String roundName;
    private MatchStatus status;
    private Integer pelotasScore;
    private Integer opponentScore;
    private Integer penaltyPelotasScore;
    private Integer penaltyOpponentScore;
    private String ticketUrl;
    private String broadcastChannel;
    private String transmissionUrl;
    private String notes;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;
}

