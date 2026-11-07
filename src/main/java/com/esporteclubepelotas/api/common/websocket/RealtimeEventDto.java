package com.esporteclubepelotas.api.common.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeEventDto {

    private String topic;
    private String action;
    private String entityId;
    private Boolean isYouth;
    private String timestamp;
}

