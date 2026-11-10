package com.esporteclubepelotas.api.modules.football.dto;

import com.esporteclubepelotas.api.modules.football.entity.SquadCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSquadDto {

    @Schema(description = "ID do campeonato oficial vinculado", example = "00000000-0000-4000-a000-000000000001")
    private String championshipId;

    @Schema(description = "ID do atleta/staff treinador vinculado", example = "455ed6c8-95e5-46b2-8435-e883b50b49f9")
    private String coachId;

    @NotNull(message = "O ano da temporada é obrigatório.")
    @Min(value = 1908, message = "O ano deve ser no mínimo 1908.")
    @Schema(description = "Ano da temporada", example = "2026")
    private Integer year;

    @NotNull(message = "A categoria do elenco é obrigatória.")
    @Schema(description = "Categoria do elenco", example = "PROFESSIONAL")
    private SquadCategory category;

    @NotBlank(message = "O nome da temporada é obrigatório.")
    @Schema(description = "Nome descritivo da temporada / competição", example = "Temporada 2026 - Gauchão")
    private String seasonName;

    @Schema(description = "Treinador principal do time", example = "Leocir Dall’Astra")
    private String coachName;

    @Valid
    @Schema(description = "Membros da comissão técnica")
    private List<SquadStaffDto> staff;

    @Schema(description = "Foto oficial do elenco perfilado")
    private String coverUrl;

    @Builder.Default
    @Schema(description = "Define se é o elenco ativo atual", example = "false")
    private Boolean isCurrent = false;

    @Builder.Default
    @Schema(description = "Status ativo do elenco", example = "true")
    private Boolean isActive = true;

    @Valid
    @Schema(description = "Atletas integrantes do elenco")
    private List<SquadMemberDto> members;
}
