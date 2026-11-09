package com.esporteclubepelotas.api.modules.membership.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualSocioOverrideDto {

    private boolean isSocio;

    private String status; // ACTIVE, SUSPENDED, INACTIVE

    @NotBlank(message = "A justificativa da alteração manual é obrigatória.")
    private String reason;
}

