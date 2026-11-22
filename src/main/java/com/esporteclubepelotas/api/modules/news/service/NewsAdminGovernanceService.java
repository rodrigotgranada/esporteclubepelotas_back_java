package com.esporteclubepelotas.api.modules.news.service;

import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.news.dto.CreateCategoryDto;
import com.esporteclubepelotas.api.modules.news.dto.GetNewsCommentsResponseDto;
import com.esporteclubepelotas.api.modules.news.dto.NewsArticleDto;
import com.esporteclubepelotas.api.modules.news.dto.NewsCategoryDto;
import com.esporteclubepelotas.api.modules.news.dto.NewsCommentDto;
import com.esporteclubepelotas.api.modules.news.dto.ShowcaseResponseDto;
import com.esporteclubepelotas.api.modules.news.dto.UpdateShowcaseDto;
import com.esporteclubepelotas.api.modules.news.entity.NewsCategory;
import com.esporteclubepelotas.api.modules.news.entity.NewsCommentStatus;
import com.esporteclubepelotas.api.modules.news.helper.NewsCommentsAdminHelper;
import com.esporteclubepelotas.api.modules.news.helper.NewsMapperHelper;
import com.esporteclubepelotas.api.modules.news.helper.NewsScheduledHelper;
import com.esporteclubepelotas.api.modules.news.helper.NewsShowcaseHelper;
import com.esporteclubepelotas.api.modules.news.helper.NewsSlugHelper;
import com.esporteclubepelotas.api.modules.news.repository.NewsArticleRepository;
import com.esporteclubepelotas.api.modules.news.repository.NewsCategoryRepository;
import com.esporteclubepelotas.api.modules.news.repository.NewsCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsAdminGovernanceService {

    private final NewsArticleRepository articleRepository;
    private final NewsCategoryRepository categoryRepository;
    private final NewsCommentRepository commentRepository;
    private final PortalWebSocketHandler webSocketHandler;

    @Transactional
    @CacheEvict(value = {"news_featured", "news_categories"}, allEntries = true)
    public NewsCategoryDto createCategory(CreateCategoryDto dto) {
        String baseSlug = NewsSlugHelper.slugify(dto.getName());
        String slug = baseSlug;
        int counter = 1;
        while (categoryRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        NewsCategory category = NewsCategory.builder()
                .name(dto.getName().trim())
                .slug(slug)
                .description(dto.getDescription() != null ? dto.getDescription().trim() : null)
                .isActive(true)
                .displayOrder(0)
                .build();

        NewsCategory saved = categoryRepository.save(category);
        return NewsMapperHelper.mapCategory(saved);
    }

    @Transactional(readOnly = true)
    public List<NewsCategoryDto> adminFindAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAscNameAsc().stream()
                .map(NewsMapperHelper::mapCategory)
                .toList();
    }

    @Transactional(readOnly = true)
    public GetNewsCommentsResponseDto adminFindComments(
            Integer page, Integer limit, NewsCommentStatus status,
            String search, String articleId, Boolean reportedOnly) {
        return NewsCommentsAdminHelper.adminFindComments(commentRepository, page, limit, status, search, articleId, reportedOnly);
    }

    @Transactional
    public NewsCommentDto adminDismissCommentReport(UUID commentId) {
        NewsCommentDto dto = NewsCommentsAdminHelper.adminDismissCommentReport(commentRepository, commentId);
        webSocketHandler.broadcastAfterCommit("news-comments", "UPDATED", commentId.toString());
        return dto;
    }

    @Transactional
    public NewsCommentDto adminUpdateCommentStatus(UUID commentId, NewsCommentStatus status) {
        NewsCommentDto dto = NewsCommentsAdminHelper.adminUpdateCommentStatus(commentRepository, commentId, status);
        webSocketHandler.broadcastAfterCommit("news-comments", "UPDATED", commentId.toString());
        return dto;
    }

    @Transactional
    public void adminDeleteComment(UUID commentId) {
        NewsCommentsAdminHelper.adminDeleteComment(commentRepository, commentId);
        webSocketHandler.broadcastAfterCommit("news-comments", "DELETED", commentId.toString());
    }

    @Transactional(readOnly = true)
    public ShowcaseResponseDto getShowcase() {
        return NewsShowcaseHelper.getShowcase(articleRepository);
    }

    @Transactional
    @CacheEvict(value = {"news_featured", "news_categories"}, allEntries = true)
    public void updateShowcase(UpdateShowcaseDto dto) {
        NewsShowcaseHelper.updateShowcase(articleRepository, dto);
        webSocketHandler.broadcastAfterCommit("news", "SHOWCASE_UPDATED", null, null);
    }

    @Transactional(readOnly = true)
    public NewsArticleDto getCurrentMain() {
        return NewsShowcaseHelper.getCurrentMain(articleRepository);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getScheduledArticles() {
        return NewsScheduledHelper.getScheduledArticles(articleRepository);
    }
}

