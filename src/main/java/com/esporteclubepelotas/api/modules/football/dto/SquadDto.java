package com.esporteclubepelotas.api.modules.football.dto;

import com.esporteclubepelotas.api.modules.football.entity.SquadCategory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SquadDto {

    private String id;

    @JsonProperty("_id")
    private String mongoId;

    private String championshipId;
    private String coachId;
    private String coachPhotoUrl;

    private Integer year;
    private SquadCategory category;
    private String seasonName;
    private String coachName;
    private List<SquadStaffDto> staff;
    private String coverUrl;

    @JsonProperty("isCurrent")
    private boolean isCurrent;

    private List<SquadMemberDto> members;
    private SquadSectorsDto sectors;

    @JsonProperty("isActive")
    private boolean isActive;

    private Instant createdAt;
    private Instant updatedAt;
}
