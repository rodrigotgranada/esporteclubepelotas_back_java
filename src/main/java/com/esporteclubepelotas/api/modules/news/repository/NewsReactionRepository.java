package com.esporteclubepelotas.api.modules.news.repository;

import com.esporteclubepelotas.api.modules.news.entity.NewsReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsReactionRepository extends JpaRepository<NewsReaction, UUID> {

    Optional<NewsReaction> findByArticleIdAndUserId(UUID articleId, String userId);

    void deleteByArticleIdAndUserId(UUID articleId, String userId);

    long countByArticleId(UUID articleId);
}
