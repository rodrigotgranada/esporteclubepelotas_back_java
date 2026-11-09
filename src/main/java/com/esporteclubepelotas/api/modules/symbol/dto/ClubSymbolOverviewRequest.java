package com.esporteclubepelotas.api.modules.symbol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubSymbolOverviewRequest {

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 150, message = "O título não pode exceder 150 caracteres")
    private String title;

    @NotBlank(message = "A descrição geral é obrigatória")
    private String description;
}

