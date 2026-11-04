package com.esporteclubepelotas.api.modules.theme.dto;

import com.esporteclubepelotas.api.modules.theme.entity.Theme;
import com.esporteclubepelotas.api.modules.theme.entity.ThemePalette;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThemeResponseDto {

    private String id;

    @JsonProperty("_id")
    private String mongoId;

    private String name;

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonProperty("isDefault")
    private boolean isDefault;

    @JsonProperty("isCampaign")
    private boolean isCampaign;

    private String campaignColor;

    private Instant startDate;

    private Instant endDate;

    private ThemePalette light;

    private ThemePalette dark;

    private Instant createdAt;

    private Instant updatedAt;

    public static ThemeResponseDto fromEntity(Theme theme) {
        if (theme == null) return null;
        String idStr = theme.getId() != null ? theme.getId().toString() : null;
        return ThemeResponseDto.builder()
                .id(idStr)
                .mongoId(idStr)
                .name(theme.getName())
                .isActive(theme.isActive())
                .isDefault(theme.isDefault())
                .isCampaign(theme.isCampaign())
                .campaignColor(theme.getCampaignColor())
                .startDate(theme.getStartDate())
                .endDate(theme.getEndDate())
                .light(theme.getLight())
                .dark(theme.getDark())
                .createdAt(theme.getCreatedAt())
                .updatedAt(theme.getUpdatedAt())
                .build();
    }
}
