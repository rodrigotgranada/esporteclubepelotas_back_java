package com.esporteclubepelotas.api.modules.sponsor.dto;

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

    private String websiteUrl;

    @Builder.Default
    private Boolean isFeatured = false;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Integer displayOrder = 0;

    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
}

