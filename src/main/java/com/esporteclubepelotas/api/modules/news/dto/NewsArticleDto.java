package com.esporteclubepelotas.api.modules.news.dto;

import com.esporteclubepelotas.api.modules.news.entity.NewsFeaturedSlot;
import com.esporteclubepelotas.api.modules.news.entity.NewsReactionsCounter;
import com.esporteclubepelotas.api.modules.news.entity.NewsStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsArticleDto {

    private String id;

    @JsonProperty("_id")
    private String mongoId;

    private String title;
    private String subtitle;
    private String slug;
    private List<String> previousSlugs;
    private String coverImage;
    private String coverCaption;
    private Map<String, Object> content;
    private String summary;
    private NewsCategoryDto category;
    private List<String> tags;
    private NewsStatus status;

    @JsonProperty("isFeatured")
    private boolean isFeatured;

    private NewsFeaturedSlot featuredSlot;

    @JsonProperty("isPinned")
    private boolean isPinned;

    @JsonProperty("showOnHomepage")
    private boolean showOnHomepage;

    private String reporterName;
    private String photographerName;
    private NewsArticleAuthorDto author;
    private Instant publishedAt;
    private List<String> gallery;
    private String youtubeVideoUrl;
    private Long viewsCount;

    @JsonProperty("allowReactions")
    private boolean allowReactions;

    @JsonProperty("allowComments")
    private boolean allowComments;

    private NewsReactionsCounter reactions;
    private NewsArticleAuthorDto lastEditedBy;

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonProperty("_redirectTo")
    private String redirectTo;

    private Instant createdAt;
    private Instant updatedAt;
}
