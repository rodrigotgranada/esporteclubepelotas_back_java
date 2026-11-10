package com.esporteclubepelotas.api.modules.kit.dto;

import com.esporteclubepelotas.api.modules.kit.entity.ClubKit;
import com.esporteclubepelotas.api.modules.kit.entity.KitType;
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
public class ClubKitResponse {
    private UUID id;
    private UUID championshipId;
    private String name;
    private KitType kitType;
    private String competition;
    private Integer seasonYear;
    private String imageUrl;
    private List<String> galleryUrls;
    private String manufacturer;
    private String sponsor;
    private String description;
    private Boolean isCurrent;
    private Boolean isActive;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;

    public static ClubKitResponse fromEntity(ClubKit kit) {
        if (kit == null) return null;

        UUID champId = kit.getChampionship() != null ? kit.getChampionship().getId() : null;
        String compName = kit.getChampionship() != null && kit.getChampionship().getName() != null
                ? kit.getChampionship().getName()
                : kit.getCompetition();
        Integer year = kit.getChampionship() != null && kit.getChampionship().getSeasonYear() != null
                ? kit.getChampionship().getSeasonYear()
                : kit.getSeasonYear();

        return ClubKitResponse.builder()
                .id(kit.getId())
                .championshipId(champId)
                .name(kit.getName())
                .kitType(kit.getKitType())
                .competition(compName)
                .seasonYear(year)
                .imageUrl(kit.getImageUrl())
                .galleryUrls(kit.getGalleryUrls())
                .manufacturer(kit.getManufacturer())
                .sponsor(kit.getSponsor())
                .description(kit.getDescription())
                .isCurrent(kit.getIsCurrent())
                .isActive(kit.getIsActive())
                .displayOrder(kit.getDisplayOrder())
                .createdAt(kit.getCreatedAt())
                .updatedAt(kit.getUpdatedAt())
                .build();
    }
}

