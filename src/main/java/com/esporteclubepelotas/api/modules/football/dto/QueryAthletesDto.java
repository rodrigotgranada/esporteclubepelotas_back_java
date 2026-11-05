package com.esporteclubepelotas.api.modules.football.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryAthletesDto {

    @Builder.Default
    @Schema(description = "Página", defaultValue = "1")
    private Integer page = 1;

    @Builder.Default
    @Schema(description = "Limite por página", defaultValue = "20")
    private Integer limit = 20;

    @Schema(description = "Termo de busca por nome, apelido ou posição")
    private String search;

    @Schema(description = "Filtrar por posição específica")
    private String position;

    @Schema(description = "Filtrar por status ativo/inativo")
    private Boolean isActive;
}

