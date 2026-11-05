package com.esporteclubepelotas.api.modules.news.helper;

import com.esporteclubepelotas.api.modules.news.dto.*;
import com.esporteclubepelotas.api.modules.news.entity.NewsArticle;
import com.esporteclubepelotas.api.modules.news.entity.NewsCategory;
import com.esporteclubepelotas.api.modules.news.entity.NewsComment;
import com.esporteclubepelotas.api.modules.user.entity.User;

public final class NewsMapperHelper {

    private NewsMapperHelper() {}

    public static NewsCategoryDto mapCategory(NewsCategory category) {
        if (category == null) return null;
        String idStr = category.getId() != null ? category.getId().toString() : null;
        return NewsCategoryDto.builder()
                .id(idStr)
                .mongoId(idStr)
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .isActive(category.isActive())
                .order(category.getDisplayOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public static NewsArticleAuthorDto mapAuthor(User author) {
        if (author == null) return null;
        String idStr = author.getId() != null ? author.getId().toString() : null;
        String roleName = author.getRole() != null ? author.getRole().getName() : "USER";
        return NewsArticleAuthorDto.builder()
                .id(idStr)
                .mongoId(idStr)
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .email(author.getEmail())
                .avatar(author.getAvatarUrl())
                .avatarUrl(author.getAvatarUrl())
                .role(roleName)
                .build();
    }

    public static NewsArticleDto mapArticle(NewsArticle article) {
        if (article == null) return null;
        String idStr = article.getId() != null ? article.getId().toString() : null;

        return NewsArticleDto.builder()
                .id(idStr)
                .mongoId(idStr)
                .title(article.getTitle())
                .subtitle(article.getSubtitle())
                .slug(article.getSlug())
                .previousSlugs(article.getPreviousSlugs())
                .coverImage(article.getCoverImage())
                .coverCaption(article.getCoverCaption())
                .content(article.getContent())
                .summary(article.getSummary())
                .category(mapCategory(article.getCategory()))
                .tags(article.getTags())
                .status(article.getStatus())
                .isFeatured(article.isFeatured())
                .featuredSlot(article.getFeaturedSlot())
                .isPinned(article.isPinned())
                .reporterName(article.getReporterName())
                .photographerName(article.getPhotographerName())
                .author(mapAuthor(article.getAuthor()))
                .publishedAt(article.getPublishedAt())
                .gallery(article.getGallery())
                .youtubeVideoUrl(article.getYoutubeVideoUrl())
                .viewsCount(article.getViewsCount())
                .allowReactions(article.isAllowReactions())
                .allowComments(article.isAllowComments())
                .reactions(article.getReactions())
                .lastEditedBy(mapAuthor(article.getLastEditedBy()))
                .isActive(article.isActive())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }

    public static NewsCommentDto mapComment(NewsComment comment) {
        if (comment == null) return null;
        String idStr = comment.getId() != null ? comment.getId().toString() : null;

        NewsCommentAuthorDto authorDto = null;
        if (comment.getAuthor() != null) {
            String authorId = comment.getAuthor().getId() != null ? comment.getAuthor().getId().toString() : null;
            String roleName = comment.getAuthor().getRole() != null ? comment.getAuthor().getRole().getName() : "TORCEDOR";
            authorDto = NewsCommentAuthorDto.builder()
                    .id(authorId)
                    .mongoId(authorId)
                    .firstName(comment.getAuthor().getFirstName() != null ? comment.getAuthor().getFirstName() : "Torcedor")
                    .lastName(comment.getAuthor().getLastName())
                    .email(comment.getAuthor().getEmail())
                    .avatar(comment.getAuthor().getAvatarUrl())
                    .avatarUrl(comment.getAuthor().getAvatarUrl())
                    .role(roleName)
                    .build();
        }

        NewsCommentArticleDto articleDto = null;
        if (comment.getArticle() != null) {
            String articleId = comment.getArticle().getId() != null ? comment.getArticle().getId().toString() : null;
            articleDto = NewsCommentArticleDto.builder()
                    .id(articleId)
                    .mongoId(articleId)
                    .title(comment.getArticle().getTitle())
                    .slug(comment.getArticle().getSlug())
                    .coverImage(comment.getArticle().getCoverImage())
                    .build();
        }

        return NewsCommentDto.builder()
                .id(idStr)
                .mongoId(idStr)
                .content(comment.getContent())
                .status(comment.getStatus())
                .createdAt(comment.getCreatedAt())
                .author(authorDto)
                .article(articleDto)
                .build();
    }
}

