package com.esporteclubepelotas.api.modules.sponsor.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSponsorDto {

    @NotBlank(message = "O nome do patrocinador é obrigatório.")
    private String name;

    @NotBlank(message = "A URL do logotipo é obrigatória.")
    private String logoUrl;

    @JsonProperty("logoDarkUrl")
    @JsonAlias({"logo_dark_url"})
    private String logoDarkUrl;

    private String websiteUrl;

    @JsonProperty("isFeatured")
    @JsonAlias({"featured", "is_featured"})
    @Builder.Default
    private Boolean isFeatured = false;

    @JsonProperty("isActive")
    @JsonAlias({"active", "is_active"})
    @Builder.Default
    private Boolean isActive = true;

    @JsonProperty("showOnMain")
    @JsonAlias({"show_on_main"})
    @Builder.Default
    private Boolean showOnMain = true;

    @JsonProperty("showOnYouth")
    @JsonAlias({"show_on_youth"})
    @Builder.Default
    private Boolean showOnYouth = false;

    @JsonProperty("showOnFemale")
    @JsonAlias({"show_on_female"})
    @Builder.Default
    private Boolean showOnFemale = false;

    @Builder.Default
    private Integer displayOrder = 0;

    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
}

