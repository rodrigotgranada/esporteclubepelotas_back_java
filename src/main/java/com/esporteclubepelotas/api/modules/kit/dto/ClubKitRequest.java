package com.esporteclubepelotas.api.modules.kit.dto;

import com.esporteclubepelotas.api.modules.kit.entity.KitType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubKitRequest {

    @NotBlank(message = "O nome do uniforme é obrigatório")
    @Size(max = 150, message = "O nome não pode exceder 150 caracteres")
    private String name;

    @NotNull(message = "O tipo do uniforme é obrigatório (HOME, AWAY, THIRD, GOALKEEPER, SPECIAL)")
    private KitType kitType;

    private java.util.UUID championshipId;

    @NotBlank(message = "A competição ou campeonato é obrigatório")
    @Size(max = 150, message = "O nome da competição não pode exceder 150 caracteres")
    private String competition;

    @NotNull(message = "O ano da temporada é obrigatório")
    @Min(value = 1900, message = "Ano de temporada inválido")
    private Integer seasonYear;

    @NotBlank(message = "A imagem principal do uniforme é obrigatória")
    private String imageUrl;

    private List<String> galleryUrls;

    @Size(max = 100, message = "O fornecedor não pode exceder 100 caracteres")
    private String manufacturer;

    @Size(max = 150, message = "O patrocinador não pode exceder 150 caracteres")
    private String sponsor;

    private String description;

    @Builder.Default
    private Boolean isCurrent = false;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Integer displayOrder = 0;
}

