package com.esporteclubepelotas.api.modules.football.dto;

import com.esporteclubepelotas.api.modules.football.entity.AthleteGender;
import com.esporteclubepelotas.api.modules.football.entity.AthleteType;
import com.esporteclubepelotas.api.modules.football.entity.PreferredFoot;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AthleteDto {

    private String id;

    @JsonProperty("_id")
    private String mongoId;

    private String name;
    private String nickname;
    private String slug;
    private String photoUrl;
    private AthleteGender gender;
    private AthleteType athleteType;
    private String youthCategory;
    private Integer entryYear;
    private String staffRole;
    private String birthDate;
    private Integer age;
    private String birthPlace;
    private Integer height;
    private Integer weight;
    private PreferredFoot preferredFoot;
    private String primaryPosition;
    private List<String> secondaryPositions;
    private List<String> pastClubs;
    private String biography;
    private String instagram;

    @Builder.Default
    private List<AthleteSeasonDto> seasons = new ArrayList<>();

    @JsonProperty("isActive")
    private boolean isActive;

    private Instant createdAt;
    private Instant updatedAt;
}
