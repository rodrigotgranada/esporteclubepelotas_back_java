package com.esporteclubepelotas.api.modules.news.helper;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.modules.news.dto.GetNewsCommentsResponseDto;
import com.esporteclubepelotas.api.modules.news.dto.NewsCommentDto;
import com.esporteclubepelotas.api.modules.news.entity.NewsComment;
import com.esporteclubepelotas.api.modules.news.entity.NewsCommentStatus;
import com.esporteclubepelotas.api.modules.news.repository.NewsCommentRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class NewsCommentsAdminHelper {

    private NewsCommentsAdminHelper() {}

    public static GetNewsCommentsResponseDto adminFindComments(
            NewsCommentRepository repository,
            Integer page,
            Integer limit,
            NewsCommentStatus status,
            String search,
            String articleId,
            Boolean reportedOnly
    ) {
        int safePage = page != null && page > 0 ? page : 1;
        int safeLimit = limit != null && limit > 0 ? Math.min(50, limit) : 10;

        Specification<NewsComment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("isActive")));

            if (Boolean.TRUE.equals(reportedOnly)) {
                predicates.add(cb.isTrue(root.get("isReported")));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (articleId != null && !articleId.isBlank()) {
                try {
                    UUID artUuid = UUID.fromString(articleId);
                    predicates.add(cb.equal(root.get("article").get("id"), artUuid));
                } catch (IllegalArgumentException ignored) {}
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("content")), pattern));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<NewsComment> result = repository.findAll(
                spec,
                PageRequest.of(safePage - 1, safeLimit, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        List<NewsCommentDto> dtos = result.getContent().stream()
                .map(NewsMapperHelper::mapComment)
                .toList();

        return GetNewsCommentsResponseDto.builder()
                .data(dtos)
                .total(result.getTotalElements())
                .page(safePage)
                .limit(safeLimit)
                .totalPages(result.getTotalPages() > 0 ? result.getTotalPages() : 1)
                .hasMore(safePage < result.getTotalPages())
                .build();
    }

    public static NewsCommentDto adminUpdateCommentStatus(
            NewsCommentRepository repository,
            UUID commentId,
            NewsCommentStatus status
    ) {
        NewsComment comment = repository.findById(commentId)
                .filter(NewsComment::isActive)
                .orElseThrow(() -> new BusinessException("Comentário não encontrado."));

        comment.setStatus(status);
        NewsComment saved = repository.save(comment);
        return NewsMapperHelper.mapComment(saved);
    }

    public static NewsCommentDto adminDismissCommentReport(
            NewsCommentRepository repository,
            UUID commentId
    ) {
        NewsComment comment = repository.findById(commentId)
                .filter(NewsComment::isActive)
                .orElseThrow(() -> new BusinessException("Comentário não encontrado."));

        comment.setReported(false);
        comment.setReportCount(0);
        comment.setLastReportReason(null);
        comment.setStatus(NewsCommentStatus.ACTIVE);
        NewsComment saved = repository.save(comment);
        return NewsMapperHelper.mapComment(saved);
    }

    public static NewsCommentDto publicCommentReport(
            NewsCommentRepository repository,
            UUID commentId,
            String reason
    ) {
        NewsComment comment = repository.findById(commentId)
                .filter(NewsComment::isActive)
                .orElseThrow(() -> new BusinessException("Comentário não encontrado."));

        comment.setReported(true);
        comment.setReportCount(comment.getReportCount() + 1);
        if (reason != null && !reason.isBlank()) {
            comment.setLastReportReason(reason.trim());
        }
        comment.setLastReportedAt(Instant.now());
        NewsComment saved = repository.save(comment);
        return NewsMapperHelper.mapComment(saved);
    }

    public static void adminDeleteComment(NewsCommentRepository repository, UUID commentId) {
        NewsComment comment = repository.findById(commentId)
                .filter(NewsComment::isActive)
                .orElseThrow(() -> new BusinessException("Comentário não encontrado."));

        comment.setActive(false);
        repository.save(comment);
    }
}
