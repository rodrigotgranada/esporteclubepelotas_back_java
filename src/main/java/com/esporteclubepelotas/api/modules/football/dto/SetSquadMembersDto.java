package com.esporteclubepelotas.api.modules.football.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetSquadMembersDto {

    @Valid
    @NotNull(message = "A lista de membros não pode ser nula.")
    @Builder.Default
    @Schema(description = "Lista completa de membros do elenco")
    private List<SquadMemberDto> members = new ArrayList<>();
}

