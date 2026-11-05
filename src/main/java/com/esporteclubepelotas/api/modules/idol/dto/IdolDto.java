package com.esporteclubepelotas.api.modules.idol.dto;

import com.esporteclubepelotas.api.modules.idol.entity.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdolDto {
    private String id;

    @JsonProperty("_id")
    public String getMongoId() {
        return id;
    }

    private String name;
    private String nickname;
    private String slug;
    private IdolCategory category;
    private String photoUrl;
    private List<String> gallery;
    private List<IdolGalleryItem> galleryItems;
    private Integer startYear;
    private Integer endYear;
    private List<IdolPeriod> periods;
    private Boolean isActiveInClub;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private String birthPlace;
    private String quote;
    private String biography;

    private IdolPlayerStats playerStats;
    private IdolCoachStats coachStats;
    private IdolStaffStats staffStats;
    private IdolFanStats fanStats;

    @JsonProperty("isFeatured")
    private boolean isFeatured;

    @JsonProperty("order")
    private int order;

    @JsonProperty("isActive")
    private boolean isActive;

    private Instant createdAt;
    private Instant updatedAt;

    public static IdolDto fromEntity(Idol idol) {
        if (idol == null) return null;

        return IdolDto.builder()
                .id(idol.getId())
                .name(idol.getName())
                .nickname(idol.getNickname())
                .slug(idol.getSlug())
                .category(idol.getCategory())
                .photoUrl(idol.getPhotoUrl())
                .gallery(idol.getGallery())
                .galleryItems(idol.getGalleryItems())
                .startYear(idol.getStartYear())
                .endYear(idol.getEndYear())
                .periods(idol.getPeriods())
                .isActiveInClub(idol.isActiveInClub())
                .birthDate(idol.getBirthDate())
                .deathDate(idol.getDeathDate())
                .birthPlace(idol.getBirthPlace())
                .quote(idol.getQuote())
                .biography(idol.getBiography())
                .playerStats(idol.getPlayerStats())
                .coachStats(idol.getCoachStats())
                .staffStats(idol.getStaffStats())
                .fanStats(idol.getFanStats())
                .isFeatured(idol.isFeatured())
                .order(idol.getDisplayOrder())
                .isActive(idol.isActive())
                .createdAt(idol.getCreatedAt())
                .updatedAt(idol.getUpdatedAt())
                .build();
    }
}

