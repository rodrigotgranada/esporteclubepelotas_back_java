package com.esporteclubepelotas.api.modules.news.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsCommentArticleDto {

    private String id;

    @JsonProperty("_id")
    private String mongoId;

    private String title;
    private String slug;
    private String coverImage;
}

