package com.esporteclubepelotas.api.modules.symbol.dto;

import com.esporteclubepelotas.api.modules.symbol.entity.ClubSymbolOverview;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubSymbolOverviewResponse {

    private String category;
    private String title;
    private String description;
    private Instant updatedAt;

    public static ClubSymbolOverviewResponse fromEntity(ClubSymbolOverview entity) {
        if (entity == null) return null;
        return ClubSymbolOverviewResponse.builder()
                .category(entity.getCategory())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

