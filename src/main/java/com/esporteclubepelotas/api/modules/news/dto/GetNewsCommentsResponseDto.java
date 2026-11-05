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
public class GetNewsCommentsResponseDto {

    private List<NewsCommentDto> data;
    private long total;
    private int page;
    private int limit;
    private int totalPages;
    private boolean hasMore;
}

