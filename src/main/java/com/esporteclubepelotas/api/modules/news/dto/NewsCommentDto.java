package com.esporteclubepelotas.api.modules.news.dto;

import com.esporteclubepelotas.api.modules.news.entity.NewsCommentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsCommentDto {

    private String id;

    @JsonProperty("_id")
    private String mongoId;

    private String content;
    private NewsCommentStatus status;
    private Instant createdAt;

    private Boolean isReported;
    private Integer reportCount;
    private String lastReportReason;
    private Instant lastReportedAt;

    private NewsCommentAuthorDto author;
    private NewsCommentArticleDto article;
}

