package com.esporteclubepelotas.api.modules.football.dto;

import com.esporteclubepelotas.api.modules.football.entity.SquadCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSquadDto {

    @Min(value = 1908, message = "O ano deve ser no mínimo 1908.")
    @Schema(description = "Ano da temporada")
    private Integer year;

    @Schema(description = "Categoria do elenco")
    private SquadCategory category;

    @Schema(description = "Nome descritivo da temporada / competição")
    private String seasonName;

    @Schema(description = "Treinador principal do time")
    private String coachName;

    @Valid
    @Schema(description = "Membros da comissão técnica")
    private List<SquadStaffDto> staff;

    @Schema(description = "Foto oficial do elenco perfilado")
    private String coverUrl;

    @Schema(description = "Define se é o elenco ativo atual")
    private Boolean isCurrent;

    @Valid
    @Schema(description = "Atletas integrantes do elenco")
    private List<SquadMemberDto> members;

    @Schema(description = "Status ativo do elenco")
    private Boolean isActive;
}

