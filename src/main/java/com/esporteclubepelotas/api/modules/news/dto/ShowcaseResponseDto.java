package com.esporteclubepelotas.api.modules.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowcaseResponseDto {

    private NewsArticleDto main;
    private List<NewsArticleDto> secondary;
    private List<NewsArticleDto> available;
}

