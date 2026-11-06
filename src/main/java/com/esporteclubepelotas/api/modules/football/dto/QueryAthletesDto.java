package com.esporteclubepelotas.api.modules.football.dto;

import com.esporteclubepelotas.api.modules.football.entity.AthleteGender;
import com.esporteclubepelotas.api.modules.football.entity.AthleteType;
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

    @Schema(description = "Filtrar por atletas aposentados ou fora de atividade (true/false)")
    private Boolean isRetired;

    @Schema(description = "Filtrar por gênero (MALE, FEMALE)")
    private AthleteGender gender;

    @Schema(description = "Filtrar por tipo (PROFESSIONAL, YOUTH, STAFF)")
    private AthleteType athleteType;

    @Schema(description = "Escopo de gestão (PROFESSIONAL inclui Profissionais + Comissão; YOUTH inclui Base + Comissão)")
    private String scope;
}
