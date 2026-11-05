package com.esporteclubepelotas.api.modules.idol.dto;

import com.esporteclubepelotas.api.modules.idol.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateAdminIdolDto {

    @NotBlank(message = "O nome do ídolo é obrigatório")
    private String name;

    private String nickname;
    private String slug;

    @NotNull(message = "A categoria é obrigatória")
    private IdolCategory category;

    private String photoUrl;
    private List<String> gallery;
    private List<IdolGalleryItem> galleryItems;

    @NotNull(message = "O ano de início é obrigatório")
    private Integer startYear;

    private Integer endYear;
    private List<IdolPeriod> periods;
    private Boolean isActiveInClub;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private String birthPlace;
    private String quote;

    @NotBlank(message = "A biografia é obrigatória")
    private String biography;

    private IdolPlayerStats playerStats;
    private IdolCoachStats coachStats;
    private IdolStaffStats staffStats;
    private IdolFanStats fanStats;

    @JsonProperty("isFeatured")
    private Boolean isFeatured;

    @JsonProperty("order")
    private Integer order;
}

