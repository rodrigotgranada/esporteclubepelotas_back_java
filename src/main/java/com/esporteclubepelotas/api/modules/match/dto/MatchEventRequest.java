package com.esporteclubepelotas.api.modules.match.dto;

import com.esporteclubepelotas.api.modules.match.entity.MatchEventTeam;
import com.esporteclubepelotas.api.modules.match.entity.MatchEventType;
import com.esporteclubepelotas.api.modules.match.entity.MatchPeriod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchEventRequest {

    @NotNull(message = "O minuto do lance é obrigatório")
    @Min(value = 0, message = "O minuto deve ser maior ou igual a 0")
    private Integer minute;

    @Min(value = 1, message = "Acréscimo deve ser maior que 0")
    private Integer minuteExtra;

    private MatchPeriod period;

    @NotNull(message = "O tipo do evento é obrigatório")
    private MatchEventType eventType;

    private MatchEventTeam team;

    private String playerName;

    private String playerOutName;

    private String title;

    private String description;
}
