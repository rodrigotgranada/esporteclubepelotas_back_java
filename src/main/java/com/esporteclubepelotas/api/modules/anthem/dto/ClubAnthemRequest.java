package com.esporteclubepelotas.api.modules.anthem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubAnthemRequest {

    @Size(max = 200, message = "O título não pode ultrapassar 200 caracteres")
    private String title;

    @Size(max = 300, message = "O subtítulo não pode ultrapassar 300 caracteres")
    private String subtitle;

    @NotBlank(message = "A letra do hino é obrigatória")
    private String lyrics;

    @Size(max = 150, message = "O autor da letra não pode ultrapassar 150 caracteres")
    private String authorLyrics;

    @Size(max = 150, message = "O autor da música não pode ultrapassar 150 caracteres")
    private String authorMusic;

    private Integer compositionYear;

    private String audioUrl;

    private String videoUrl;

    private String coverImageUrl;

    @Size(max = 200, message = "O título histórico não pode ultrapassar 200 caracteres")
    private String historyTitle;

    private String historyText;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isOfficial = false;

    @Builder.Default
    private Integer displayOrder = 0;
}
