package com.esporteclubepelotas.api.modules.match.dto;

import com.esporteclubepelotas.api.modules.match.entity.ChampionshipCategory;
import com.esporteclubepelotas.api.modules.match.entity.RegulationItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChampionshipRequest {

    @NotBlank(message = "O nome do campeonato é obrigatório")
    @Size(min = 2, max = 160, message = "O nome deve ter entre 2 e 160 caracteres")
    private String name;

    @NotNull(message = "O ano da temporada é obrigatório")
    @Min(value = 1900, message = "Ano inválido")
    private Integer seasonYear;

    private ChampionshipCategory category;

    private String logoUrl;

    private String tableUrl;

    private List<RegulationItem> regulations;

    private Boolean isActive;

    private Integer displayOrder;
}

