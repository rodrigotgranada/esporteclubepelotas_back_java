package com.esporteclubepelotas.api.modules.football.dto;

import com.esporteclubepelotas.api.modules.football.entity.SquadCategory;
import com.esporteclubepelotas.api.modules.football.entity.SquadMemberRole;
import com.esporteclubepelotas.api.modules.football.entity.SquadSector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AthleteSeasonDto {

    private String squadId;
    private int year;
    private SquadCategory category;
    private String seasonName;
    private Integer jerseyNumber;
    private SquadSector sector;
    private SquadMemberRole role;
}

