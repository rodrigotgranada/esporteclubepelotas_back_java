package com.esporteclubepelotas.api.modules.newsletter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnsubscribeNewsletterDto {

    @NotBlank(message = "Token de descadastro inválido.")
    private String token;

    @Size(max = 255, message = "O motivo deve ter no máximo 255 caracteres.")
    private String reason;
}
