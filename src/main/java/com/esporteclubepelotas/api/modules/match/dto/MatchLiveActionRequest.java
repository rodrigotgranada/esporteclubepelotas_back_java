package com.esporteclubepelotas.api.modules.match.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchLiveActionRequest {

    @NotBlank(message = "A ação do lance a lance é obrigatória")
    private String action;

    private Integer minute;
    private String period;
    private String notes;
}

