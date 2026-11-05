package com.esporteclubepelotas.api.modules.football.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SquadStaffDto {

    @NotBlank(message = "O cargo da comissão técnica é obrigatório.")
    @Schema(description = "Cargo na comissão técnica", example = "Auxiliar Técnico")
    private String role;

    @NotBlank(message = "O nome do profissional é obrigatório.")
    @Schema(description = "Nome do profissional", example = "Felipe Endres")
    private String name;
}

