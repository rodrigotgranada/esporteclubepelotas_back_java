package com.esporteclubepelotas.api.modules.symbol.dto;

import com.esporteclubepelotas.api.modules.symbol.entity.ClubSymbol;
import com.esporteclubepelotas.api.modules.symbol.entity.SymbolCategory;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubSymbolResponse {

    private UUID id;
    private SymbolCategory category;
    private String name;
    private String subtitle;
    private String description;
    private String imageUrl;
    private List<String> galleryUrls;
    private Integer yearStart;
    private Integer yearEnd;
    private Boolean isOfficial;
    private Boolean isActive;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;

    public static ClubSymbolResponse fromEntity(ClubSymbol entity) {
        if (entity == null) return null;
        return ClubSymbolResponse.builder()
                .id(entity.getId())
                .category(entity.getCategory())
                .name(entity.getName())
                .subtitle(entity.getSubtitle())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .galleryUrls(entity.getGalleryUrls() != null ? entity.getGalleryUrls() : new ArrayList<>())
                .yearStart(entity.getYearStart())
                .yearEnd(entity.getYearEnd())
                .isOfficial(entity.getIsOfficial())
                .isActive(entity.getIsActive())
                .displayOrder(entity.getDisplayOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

