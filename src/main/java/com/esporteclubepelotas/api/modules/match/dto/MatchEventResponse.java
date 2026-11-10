package com.esporteclubepelotas.api.modules.match.dto;

import com.esporteclubepelotas.api.modules.match.entity.MatchEventTeam;
import com.esporteclubepelotas.api.modules.match.entity.MatchEventType;
import com.esporteclubepelotas.api.modules.match.entity.MatchPeriod;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchEventResponse {

    private UUID id;
    private UUID matchId;
    private Integer minute;
    private Integer minuteExtra;
    private String formattedMinute;
    private MatchPeriod period;
    private MatchEventType eventType;
    private MatchEventTeam team;
    private String playerName;
    private String playerOutName;
    private String title;
    private String description;
    private Integer scorePelotasMoment;
    private Integer scoreOpponentMoment;
    private Instant createdAt;
}

