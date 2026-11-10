package com.esporteclubepelotas.api.modules.match.dto;

import com.esporteclubepelotas.api.modules.match.entity.HomeAway;
import com.esporteclubepelotas.api.modules.match.entity.MatchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class MatchRequest {

    @NotNull(message = "O campeonato é obrigatório")
    private UUID championshipId;

    @NotBlank(message = "O nome do adversário é obrigatório")
    @Size(min = 2, max = 160, message = "O nome do adversário deve ter entre 2 e 160 caracteres")
    private String opponentName;

    private String opponentLogoUrl;

    @NotNull(message = "O mando de campo é obrigatório")
    private HomeAway homeAway;

    @NotBlank(message = "O estádio é obrigatório")
    @Size(max = 160)
    private String stadiumName;

    @Size(max = 120)
    private String city;

    @NotNull(message = "A data e horário da partida são obrigatórios")
    private Instant matchDate;

    @NotBlank(message = "A rodada ou fase é obrigatória")
    @Size(max = 100)
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
}

