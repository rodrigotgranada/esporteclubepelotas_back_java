package com.esporteclubepelotas.api.modules.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSearchResponseDto {
    private String query;
    private int totalResults;
    private List<GlobalSearchItemDto> news;
    private List<GlobalSearchItemDto> athletes;
    private List<GlobalSearchItemDto> idols;
    private List<GlobalSearchItemDto> matches;
    private List<GlobalSearchItemDto> transparency;
}

