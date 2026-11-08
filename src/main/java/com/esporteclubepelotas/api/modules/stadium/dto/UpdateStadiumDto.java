package com.esporteclubepelotas.api.modules.stadium.dto;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateStadiumDto {

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 255, message = "O título não pode ultrapassar 255 caracteres")
    private String title;

    @Size(max = 500, message = "O subtítulo não pode ultrapassar 500 caracteres")
    private String subtitle;

    private String coverImageUrl;

    @Size(max = 100, message = "A data de inauguração não pode ultrapassar 100 caracteres")
    private String inaugurationDate;

    @Size(max = 100, message = "A capacidade não pode ultrapassar 100 caracteres")
    private String capacity;

    @Size(max = 100, message = "As dimensões do gramado não podem ultrapassar 100 caracteres")
    private String pitchDimensions;

    @Size(max = 255, message = "O endereço não pode ultrapassar 255 caracteres")
    private String address;

    private String mapsDirectUrl;
    private String mapsEmbedUrl;

    @Size(max = 255, message = "O título da reportagem não pode ultrapassar 255 caracteres")
    private String storyTitle;

    @Size(max = 500, message = "O subtítulo da reportagem não pode ultrapassar 500 caracteres")
    private String storySubtitle;

    @NotBlank(message = "O conteúdo da história é obrigatório")
    private String contentHtml;

    private List<StadiumGalleryImageDto> gallery;
    private List<StadiumFactDto> facts;
    private List<StadiumQuickFactDto> quickFacts;
    private Boolean isActive;
}

