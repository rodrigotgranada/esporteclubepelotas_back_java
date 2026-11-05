package com.esporteclubepelotas.api.modules.football.dto;

import com.esporteclubepelotas.api.modules.football.entity.SquadMemberRole;
import com.esporteclubepelotas.api.modules.football.entity.SquadSector;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SquadMemberDto {

    @NotBlank(message = "O ID do atleta é obrigatório.")
    @Schema(description = "ID do Atleta cadastrado", example = "507f1f77bcf86cd799439011")
    private String athleteId;

    private AthleteDto athlete;

    @NotNull(message = "O número da camisa é obrigatório.")
    @Min(value = 1, message = "O número da camisa deve ser no mínimo 1.")
    @Schema(description = "Número da camisa", example = "9")
    private Integer jerseyNumber;

    @NotNull(message = "O setor de atuação é obrigatório.")
    @Schema(description = "Setor de atuação no elenco", example = "FORWARD")
    private SquadSector sector;

    @Builder.Default
    @Schema(description = "Papel do atleta no elenco", example = "STARTER")
    private SquadMemberRole role = SquadMemberRole.STARTER;

    @Builder.Default
    @JsonProperty("order")
    @Schema(description = "Ordem de exibição visual", defaultValue = "0")
    private Integer order = 0;
}

