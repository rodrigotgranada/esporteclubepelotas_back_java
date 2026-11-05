package com.esporteclubepelotas.api.modules.football.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SquadSeasonsResponseDto {

    private List<Integer> years;
    private List<String> categories;
    private Map<Integer, List<String>> categoriesByYear;

    @JsonProperty("isModuleActive")
    private boolean isModuleActive;
}
