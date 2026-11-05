package com.esporteclubepelotas.api.modules.news.repository;

import com.esporteclubepelotas.api.modules.news.entity.NewsComment;
import com.esporteclubepelotas.api.modules.news.entity.NewsCommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NewsCommentRepository extends JpaRepository<NewsComment, UUID>, JpaSpecificationExecutor<NewsComment> {

    Page<NewsComment> findByArticleIdAndIsActiveTrueAndStatusOrderByCreatedAtDesc(
            UUID articleId,
            NewsCommentStatus status,
            Pageable pageable
    );

    long countByArticleIdAndIsActiveTrueAndStatus(UUID articleId, NewsCommentStatus status);
}

