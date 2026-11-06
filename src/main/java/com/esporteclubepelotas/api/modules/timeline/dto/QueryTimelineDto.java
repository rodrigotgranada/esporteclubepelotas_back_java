package com.esporteclubepelotas.api.modules.timeline.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryTimelineDto {

    @Builder.Default
    @Schema(description = "Página", defaultValue = "1")
    private Integer page = 1;

    @Builder.Default
    @Schema(description = "Limite por página", defaultValue = "50")
    private Integer limit = 50;

    @Schema(description = "Termo de busca por título ou descrição")
    private String search;

    @Schema(description = "Filtrar por ano específico")
    private Integer year;

    @Schema(description = "Filtrar por slug ou ID de categoria")
    private String category;

    @Schema(description = "Filtrar por destaque")
    private Boolean isHighlighted;

    @Schema(description = "Filtrar por status ativo")
    private Boolean isActive;
}
