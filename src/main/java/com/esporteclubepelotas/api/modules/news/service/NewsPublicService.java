package com.esporteclubepelotas.api.modules.news.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.news.dto.*;
import com.esporteclubepelotas.api.modules.news.entity.*;
import com.esporteclubepelotas.api.modules.news.helper.NewsMapperHelper;
import com.esporteclubepelotas.api.modules.news.repository.*;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsPublicService {

    private final NewsArticleRepository articleRepository;
    private final NewsCategoryRepository categoryRepository;
    private final NewsCommentRepository commentRepository;
    private final NewsReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final PortalWebSocketHandler webSocketHandler;

    private User validateActiveUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException("Usuário não autenticado.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

        if (user.getStatus() != UserStatus.ACTIVE || !user.isActive()) {
            throw new BusinessException("Sua conta precisa estar ativa e com o e-mail verificado para interagir.");
        }
        return user;
    }

    private NewsArticle findActiveArticleBySlugOrId(String slugOrId) {
        String clean = slugOrId.trim().toLowerCase();
        try {
            UUID id = UUID.fromString(slugOrId.trim());
            Optional<NewsArticle> byId = articleRepository.findById(id).filter(NewsArticle::isActive);
            if (byId.isPresent()) return byId.get();
        } catch (IllegalArgumentException ignored) {}

        return articleRepository.findBySlugAndIsActiveTrue(clean)
                .orElseGet(() -> articleRepository.findByPreviousSlugNative("[\"" + clean + "\"]")
                        .orElseThrow(() -> new BusinessException("Matéria não encontrada ou indisponível.")));
    }

    @Transactional(readOnly = true)
    public GetNewsResponseDto findAll(QueryNewsDto query) {
        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int limit = query.getLimit() != null && query.getLimit() > 0 ? Math.min(50, query.getLimit()) : 10;
        Instant now = Instant.now();

        Specification<NewsArticle> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("isActive")));

            Predicate publishedCond = cb.and(
                    cb.equal(root.get("status"), NewsStatus.PUBLISHED),
                    cb.lessThanOrEqualTo(root.get("publishedAt"), now)
            );
            Predicate scheduledCond = cb.and(
                    cb.equal(root.get("status"), NewsStatus.SCHEDULED),
                    cb.lessThanOrEqualTo(root.get("publishedAt"), now)
            );
            predicates.add(cb.or(publishedCond, scheduledCond));

            if (query.getCategory() != null && !query.getCategory().isBlank()) {
                String catParam = query.getCategory().trim().toLowerCase();
                try {
                    UUID catUuid = UUID.fromString(catParam);
                    predicates.add(cb.equal(root.get("category").get("id"), catUuid));
                } catch (IllegalArgumentException ex) {
                    predicates.add(cb.equal(cb.lower(root.get("category").get("slug")), catParam));
                }
            }

            if (query.getSearch() != null && !query.getSearch().isBlank()) {
                String pattern = "%" + query.getSearch().trim().toLowerCase() + "%";
                Predicate searchPred = cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("subtitle")), pattern),
                        cb.like(cb.lower(root.get("summary")), pattern)
                );
                predicates.add(searchPred);
            }

            if (query.getIsFeatured() != null) {
                predicates.add(cb.equal(root.get("isFeatured"), query.getIsFeatured()));
            }

            if (query.getFeaturedSlot() != null) {
                predicates.add(cb.equal(root.get("featuredSlot"), query.getFeaturedSlot()));
            }

            if (query.getIsPinned() != null) {
                predicates.add(cb.equal(root.get("isPinned"), query.getIsPinned()));
            }

            if (query.getShowOnHomepage() != null) {
                predicates.add(cb.equal(root.get("showOnHomepage"), query.getShowOnHomepage()));
            }

            if (query.getIsYouth() != null) {
                predicates.add(cb.equal(root.get("isYouth"), query.getIsYouth()));
            }

            if (query.getIsFemale() != null) {
                predicates.add(cb.equal(root.get("isFemale"), query.getIsFemale()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(
                Sort.Order.desc("isPinned"),
                Sort.Order.desc("isFeatured"),
                Sort.Order.desc("publishedAt")
        );

        Page<NewsArticle> paged = articleRepository.findAll(spec, PageRequest.of(page - 1, limit, sort));

        List<NewsArticleDto> dtos = paged.getContent().stream()
                .map(NewsMapperHelper::mapArticle)
                .toList();

        return GetNewsResponseDto.builder()
                .data(dtos)
                .total(paged.getTotalElements())
                .page(page)
                .limit(limit)
                .totalPages(paged.getTotalPages() > 0 ? paged.getTotalPages() : 1)
                .build();
    }

    @Transactional
    public NewsArticleDto findBySlug(String slug) {
        String cleanSlug = slug.toLowerCase().trim();
        Instant now = Instant.now();

        NewsArticle article = articleRepository.findBySlugAndIsActiveTrue(cleanSlug).orElse(null);
        boolean isRedirect = false;

        if (article == null) {
            article = articleRepository.findByPreviousSlugNative("[\"" + cleanSlug + "\"]").orElse(null);
            if (article != null) isRedirect = true;
        }

        if (article == null) {
            throw new BusinessException("Matéria não encontrada ou indisponível.");
        }

        boolean isPublic = (article.getStatus() == NewsStatus.PUBLISHED || article.getStatus() == NewsStatus.SCHEDULED)
                && article.getPublishedAt() != null && !article.getPublishedAt().isAfter(now);

        if (!isPublic) {
            throw new BusinessException("Matéria não encontrada ou indisponível.");
        }

        articleRepository.incrementViewsCount(article.getId());
        article.setViewsCount(article.getViewsCount() + 1);

        NewsArticleDto dto = NewsMapperHelper.mapArticle(article);
        if (isRedirect) {
            dto.setRedirectTo(article.getSlug());
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public List<NewsArticleDto> findFeatured(int limit) {
        int safeLimit = Math.max(1, limit);
        Instant now = Instant.now();

        List<NewsArticle> mainArticles = articleRepository.findPublicBySlot(now, NewsFeaturedSlot.MAIN, PageRequest.of(0, 1));
        List<UUID> collectedIds = new ArrayList<>(mainArticles.stream().map(NewsArticle::getId).toList());

        List<NewsArticle> secondaryArticles = new ArrayList<>();
        if (collectedIds.size() < safeLimit) {
            secondaryArticles = articleRepository.findPublicBySlot(now, NewsFeaturedSlot.SECONDARY, PageRequest.of(0, safeLimit - collectedIds.size()));
            secondaryArticles.forEach(a -> collectedIds.add(a.getId()));
        }

        List<NewsArticle> remaining = new ArrayList<>();
        if (collectedIds.size() < safeLimit) {
            Collection<UUID> excluded = collectedIds.isEmpty() ? Collections.singleton(UUID.fromString("00000000-0000-0000-0000-000000000000")) : collectedIds;
            remaining = articleRepository.findRemainingFeatured(now, excluded, PageRequest.of(0, safeLimit - collectedIds.size()));
        }

        List<NewsArticle> combined = new ArrayList<>();
        combined.addAll(mainArticles);
        combined.addAll(secondaryArticles);
        combined.addAll(remaining);

        return combined.stream()
                .limit(safeLimit)
                .map(NewsMapperHelper::mapArticle)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NewsCategoryDto> findAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAscNameAsc().stream()
                .map(NewsMapperHelper::mapCategory)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrevNextNewsResponseDto getPrevNext(String slug) {
        NewsArticle article = findActiveArticleBySlugOrId(slug);
        if (article.getPublishedAt() == null) {
            return PrevNextNewsResponseDto.builder().build();
        }

        Instant now = Instant.now();
        List<NewsArticle> prevList = articleRepository.findPreviousPublicArticle(now, article.getId(), article.getPublishedAt(), PageRequest.of(0, 1));
        List<NewsArticle> nextList = articleRepository.findNextPublicArticle(now, article.getId(), article.getPublishedAt(), PageRequest.of(0, 1));

        PrevNextNewsDto prevDto = prevList.isEmpty() ? null : PrevNextNewsDto.builder()
                .title(prevList.get(0).getTitle())
                .slug(prevList.get(0).getSlug())
                .coverImage(prevList.get(0).getCoverImage())
                .publishedAt(prevList.get(0).getPublishedAt())
                .build();

        PrevNextNewsDto nextDto = nextList.isEmpty() ? null : PrevNextNewsDto.builder()
                .title(nextList.get(0).getTitle())
                .slug(nextList.get(0).getSlug())
                .coverImage(nextList.get(0).getCoverImage())
                .publishedAt(nextList.get(0).getPublishedAt())
                .build();

        return PrevNextNewsResponseDto.builder()
                .previous(prevDto)
                .next(nextDto)
                .build();
    }


    @Transactional(readOnly = true)
    public Map<String, Object> getUserReaction(String slug, String userId) {
        Map<String, Object> response = new HashMap<>();
        if (userId == null || userId.isBlank()) {
            response.put("userReaction", null);
            return response;
        }
        NewsArticle article = findActiveArticleBySlugOrId(slug);
        Optional<NewsReaction> reactionOpt = reactionRepository.findByArticleIdAndUserId(article.getId(), userId);
        response.put("userReaction", reactionOpt.map(r -> r.getType().getValue()).orElse(null));
        return response;
    }
    @Transactional
    public ReactNewsResponseDto reactToArticle(String slug, String userId, NewsReactionType type) {
        User user = validateActiveUser(userId);
        NewsArticle article = findActiveArticleBySlugOrId(slug);

        if (!article.isAllowReactions()) {
            throw new BusinessException("As reações estão desativadas para esta publicação.");
        }

        Optional<NewsReaction> existingOpt = reactionRepository.findByArticleIdAndUserId(article.getId(), user.getId());
        NewsReactionsCounter counter = article.getReactions() != null ? article.getReactions() : new NewsReactionsCounter();
        String userReaction = null;

        if (existingOpt.isPresent() && existingOpt.get().getType() == type) {
            reactionRepository.delete(existingOpt.get());
            counter.decrement(type);
        } else if (existingOpt.isPresent()) {
            NewsReaction existing = existingOpt.get();
            counter.decrement(existing.getType());
            counter.increment(type);
            existing.setType(type);
            reactionRepository.save(existing);
            userReaction = type.getValue();
        } else {
            NewsReaction newReaction = NewsReaction.builder()
                    .article(article)
                    .user(user)
                    .type(type)
                    .build();
            reactionRepository.save(newReaction);
            counter.increment(type);
            userReaction = type.getValue();
        }

        article.setReactions(counter);
        articleRepository.save(article);

        return ReactNewsResponseDto.builder()
                .reactions(counter)
                .userReaction(userReaction)
                .build();
    }

    @Transactional(readOnly = true)
    public GetNewsCommentsResponseDto getComments(String slug, Integer page, Integer limit) {
        NewsArticle article = findActiveArticleBySlugOrId(slug);
        int safePage = page != null && page > 0 ? page : 1;
        int safeLimit = limit != null && limit > 0 ? Math.min(50, limit) : 10;

        Page<NewsComment> paged = commentRepository.findByArticleIdAndIsActiveTrueAndStatusOrderByCreatedAtDesc(
                article.getId(),
                NewsCommentStatus.ACTIVE,
                PageRequest.of(safePage - 1, safeLimit)
        );

        List<NewsCommentDto> dtos = paged.getContent().stream()
                .map(NewsMapperHelper::mapComment)
                .toList();

        return GetNewsCommentsResponseDto.builder()
                .data(dtos)
                .total(paged.getTotalElements())
                .page(safePage)
                .limit(safeLimit)
                .totalPages(paged.getTotalPages() > 0 ? paged.getTotalPages() : 1)
                .hasMore(safePage < paged.getTotalPages())
                .build();
    }

    @Transactional
    public NewsCommentDto addComment(String slug, String userId, String content) {
        User user = validateActiveUser(userId);
        NewsArticle article = findActiveArticleBySlugOrId(slug);

        if (!article.isAllowComments()) {
            throw new BusinessException("Os comentários estão desativados para esta publicação.");
        }

        NewsComment comment = NewsComment.builder()
                .article(article)
                .author(user)
                .content(content.trim())
                .status(NewsCommentStatus.ACTIVE)
                .isActive(true)
                .build();

        NewsComment saved = commentRepository.save(comment);
        return NewsMapperHelper.mapComment(saved);
    }

    @Transactional
    public void deleteComment(UUID commentId, String userId, String userRole) {
        validateActiveUser(userId);
        NewsComment comment = commentRepository.findById(commentId)
                .filter(NewsComment::isActive)
                .orElseThrow(() -> new BusinessException("Comentário não encontrado."));

        boolean isAuthor = comment.getAuthor().getId().equals(userId);
        boolean isStaff = userRole != null && List.of("Owner", "Admin", "Editor", "OWNER", "ADMIN", "EDITOR").contains(userRole);

        if (!isAuthor && !isStaff) {
            throw new BusinessException("Você não tem permissão para remover este comentário.");
        }

        comment.setActive(false);
        commentRepository.save(comment);
    }

    @Transactional
    public NewsCommentDto reportComment(UUID commentId, String reason) {
        NewsCommentDto dto = com.esporteclubepelotas.api.modules.news.helper.NewsCommentsAdminHelper.publicCommentReport(
                commentRepository, commentId, reason
        );
        webSocketHandler.broadcastAfterCommit("news-comments", "REPORTED", commentId.toString());
        return dto;
    }
}
