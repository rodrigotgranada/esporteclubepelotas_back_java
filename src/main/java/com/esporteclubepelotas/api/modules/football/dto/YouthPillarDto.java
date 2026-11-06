package com.esporteclubepelotas.api.modules.football.dto;

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
public class YouthPillarDto {

    @NotBlank(message = "O título do pilar é obrigatório.")
    @Size(max = 60, message = "O título do pilar deve ter no máximo 60 caracteres.")
    private String title;

    @NotBlank(message = "O texto do pilar é obrigatório.")
    @Size(max = 220, message = "O texto do pilar deve ter no máximo 220 caracteres.")
    private String description;
}
