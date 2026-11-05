package com.esporteclubepelotas.api.modules.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrevNextNewsDto {

    private String title;
    private String slug;
    private String coverImage;
    private Instant publishedAt;
}

