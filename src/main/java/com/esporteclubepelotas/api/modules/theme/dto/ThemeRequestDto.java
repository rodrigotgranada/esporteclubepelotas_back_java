package com.esporteclubepelotas.api.modules.theme.dto;

import com.esporteclubepelotas.api.modules.theme.entity.ThemePalette;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThemeRequestDto {

    @Size(min = 2, max = 100, message = "O nome do tema deve ter entre 2 e 100 caracteres")
    private String name;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("isDefault")
    private Boolean isDefault;

    @JsonProperty("isCampaign")
    private Boolean isCampaign;

    private String campaignColor;

    private Instant startDate;

    private Instant endDate;

    private ThemePalette light;

    private ThemePalette dark;
}
