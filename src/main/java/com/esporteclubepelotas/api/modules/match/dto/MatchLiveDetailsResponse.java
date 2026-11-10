package com.esporteclubepelotas.api.modules.match.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchLiveDetailsResponse {

    private MatchResponse match;
    private List<MatchEventResponse> events;
    private String currentPeriod;
    private String currentMinute;
    private Instant timerStartedAt;
    private Boolean isTimerRunning;
}
