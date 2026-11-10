package com.esporteclubepelotas.api.modules.match.dto;

import com.esporteclubepelotas.api.modules.match.entity.ChampionshipCategory;
import com.esporteclubepelotas.api.modules.match.entity.RegulationItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChampionshipResponse {
    private UUID id;
    private String name;
    private Integer seasonYear;
    private ChampionshipCategory category;
    private String logoUrl;
    private String tableUrl;
    private List<RegulationItem> regulations;
    private Boolean isActive;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;
}

