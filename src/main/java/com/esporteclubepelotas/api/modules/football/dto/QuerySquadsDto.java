package com.esporteclubepelotas.api.modules.football.dto;

import com.esporteclubepelotas.api.modules.football.entity.SquadCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuerySquadsDto {

    @Builder.Default
    @Schema(description = "Página", defaultValue = "1")
    private Integer page = 1;

    @Builder.Default
    @Schema(description = "Limite por página", defaultValue = "20")
    private Integer limit = 20;

    @Schema(description = "Termo de busca por competição ou comissão/treinador")
    private String search;

    @Schema(description = "Filtrar por ano da temporada")
    private Integer year;

    @Schema(description = "Filtrar por categoria do elenco")
    private SquadCategory category;

    @Schema(description = "Escopo de gestão (PROFESSIONAL para Profissional/Feminino; YOUTH para Sub-20/Sub-17/Sub-15)")
    private String scope;

    @Schema(description = "Filtrar se é o elenco ativo atual")
    private Boolean isCurrent;
}
