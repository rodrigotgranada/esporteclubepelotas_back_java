package com.esporteclubepelotas.api.modules.football.dto;

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
public class SquadSectorsDto {

    @Builder.Default
    private List<SquadMemberDto> goalkeepers = new ArrayList<>();

    @Builder.Default
    private List<SquadMemberDto> defenders = new ArrayList<>();

    @Builder.Default
    private List<SquadMemberDto> midfielders = new ArrayList<>();

    @Builder.Default
    private List<SquadMemberDto> forwards = new ArrayList<>();
}

