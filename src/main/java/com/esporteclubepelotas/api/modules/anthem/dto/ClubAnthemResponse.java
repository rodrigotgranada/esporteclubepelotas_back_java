package com.esporteclubepelotas.api.modules.anthem.dto;

import com.esporteclubepelotas.api.modules.anthem.entity.ClubAnthem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubAnthemResponse {

    private UUID id;
    private String title;
    private String subtitle;
    private String lyrics;
    private String authorLyrics;
    private String authorMusic;
    private Integer compositionYear;
    private String audioUrl;
    private String videoUrl;
    private String coverImageUrl;
    private String historyTitle;
    private String historyText;
    private Boolean isActive;
    private Boolean isOfficial;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;

    public static ClubAnthemResponse fromEntity(ClubAnthem anthem) {
        if (anthem == null) return null;
        return ClubAnthemResponse.builder()
                .id(anthem.getId())
                .title(anthem.getTitle())
                .subtitle(anthem.getSubtitle())
                .lyrics(anthem.getLyrics())
                .authorLyrics(anthem.getAuthorLyrics())
                .authorMusic(anthem.getAuthorMusic())
                .compositionYear(anthem.getCompositionYear())
                .audioUrl(anthem.getAudioUrl())
                .videoUrl(anthem.getVideoUrl())
                .coverImageUrl(anthem.getCoverImageUrl())
                .historyTitle(anthem.getHistoryTitle())
                .historyText(anthem.getHistoryText())
                .isActive(anthem.getIsActive())
                .isOfficial(anthem.getIsOfficial())
                .displayOrder(anthem.getDisplayOrder())
                .createdAt(anthem.getCreatedAt())
                .updatedAt(anthem.getUpdatedAt())
                .build();
    }
}

