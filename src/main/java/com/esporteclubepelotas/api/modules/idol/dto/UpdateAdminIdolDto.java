package com.esporteclubepelotas.api.modules.idol.dto;

import com.esporteclubepelotas.api.modules.idol.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
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
    @JsonProperty("isInMemoriam")
    private Boolean isInMemoriam;
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

    @JsonIgnore
    @Builder.Default
    private boolean endYearProvided = false;

    @JsonIgnore
    @Builder.Default
    private boolean birthDateProvided = false;

    @JsonIgnore
    @Builder.Default
    private boolean deathDateProvided = false;

    @JsonSetter("endYear")
    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
        this.endYearProvided = true;
    }

    @JsonSetter("birthDate")
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        this.birthDateProvided = true;
    }

    @JsonSetter("deathDate")
    public void setDeathDate(LocalDate deathDate) {
        this.deathDate = deathDate;
        this.deathDateProvided = true;
    }
}
