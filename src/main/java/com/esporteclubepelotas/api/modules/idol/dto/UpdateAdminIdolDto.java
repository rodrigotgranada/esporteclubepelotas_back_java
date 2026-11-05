package com.esporteclubepelotas.api.modules.idol.dto;

import com.esporteclubepelotas.api.modules.idol.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateAdminIdolDto {
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
    private Boolean isFeatured;

    @JsonProperty("order")
    private Integer order;

    @JsonProperty("isActive")
    private Boolean isActive;
}

