package com.esporteclubepelotas.api.modules.newsletter.dto;

import jakarta.validation.constraints.NotBlank;
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
}

