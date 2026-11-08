package com.esporteclubepelotas.api.modules.stadium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StadiumResponseDto {
    private Long id;
    private String title;
    private String subtitle;
    private String coverImageUrl;
    private String inaugurationDate;
    private String capacity;
    private String pitchDimensions;
    private String address;
    private String mapsDirectUrl;
    private String mapsEmbedUrl;
    private String storyTitle;
    private String storySubtitle;
    private String contentHtml;
    private List<StadiumGalleryImageDto> gallery;
    private List<StadiumFactDto> facts;
    private List<StadiumQuickFactDto> quickFacts;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}

