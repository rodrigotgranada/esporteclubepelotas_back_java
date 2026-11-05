package com.esporteclubepelotas.api.modules.news.repository;

import com.esporteclubepelotas.api.modules.news.entity.NewsArticle;
import com.esporteclubepelotas.api.modules.news.entity.NewsFeaturedSlot;
import com.esporteclubepelotas.api.modules.news.entity.NewsStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, UUID>, JpaSpecificationExecutor<NewsArticle> {

    Optional<NewsArticle> findBySlugAndIsActiveTrue(String slug);

    Optional<NewsArticle> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    @Query(value = "SELECT * FROM news_articles a WHERE a.is_active = true AND a.previous_slugs @> CAST(:slugJson AS jsonb) LIMIT 1", nativeQuery = true)
    Optional<NewsArticle> findByPreviousSlugNative(@Param("slugJson") String slugJson);

    @Query("SELECT a FROM NewsArticle a WHERE a.isActive = true AND " +
            "((a.status = 'PUBLISHED' AND a.publishedAt <= :now) OR " +
            " (a.status = 'SCHEDULED' AND a.publishedAt <= :now)) " +
            "ORDER BY a.isPinned DESC, a.isFeatured DESC, a.publishedAt DESC")
    Page<NewsArticle> findPublicFeed(@Param("now") Instant now, Pageable pageable);

    @Query("SELECT a FROM NewsArticle a WHERE a.isActive = true AND " +
            "((a.status = 'PUBLISHED' AND a.publishedAt <= :now) OR " +
            " (a.status = 'SCHEDULED' AND a.publishedAt <= :now)) AND " +
            "a.featuredSlot = :slot " +
            "ORDER BY a.publishedAt DESC")
    List<NewsArticle> findPublicBySlot(@Param("now") Instant now, @Param("slot") NewsFeaturedSlot slot, Pageable pageable);

    @Query("SELECT a FROM NewsArticle a WHERE a.isActive = true AND " +
            "((a.status = 'PUBLISHED' AND a.publishedAt <= :now) OR " +
            " (a.status = 'SCHEDULED' AND a.publishedAt <= :now)) AND " +
            "a.isFeatured = true AND a.id NOT IN :excludedIds " +
            "ORDER BY a.publishedAt DESC")
    List<NewsArticle> findRemainingFeatured(
            @Param("now") Instant now,
            @Param("excludedIds") Collection<UUID> excludedIds,
            Pageable pageable
    );

    @Query("SELECT a FROM NewsArticle a WHERE a.isActive = true AND " +
            "((a.status = 'PUBLISHED' AND a.publishedAt <= :now) OR " +
            " (a.status = 'SCHEDULED' AND a.publishedAt <= :now)) AND " +
            "a.category.id = :categoryId AND a.id != :articleId " +
            "ORDER BY a.publishedAt DESC")
    List<NewsArticle> findRelatedArticles(
            @Param("now") Instant now,
            @Param("categoryId") UUID categoryId,
            @Param("articleId") UUID articleId,
            Pageable pageable
    );

    @Query("SELECT a FROM NewsArticle a WHERE a.isActive = true AND " +
            "((a.status = 'PUBLISHED' AND a.publishedAt <= :now) OR " +
            " (a.status = 'SCHEDULED' AND a.publishedAt <= :now)) AND " +
            "a.id != :currentId AND a.publishedAt < :currentPublishedAt " +
            "ORDER BY a.publishedAt DESC")
    List<NewsArticle> findPreviousPublicArticle(
            @Param("now") Instant now,
            @Param("currentId") UUID currentId,
            @Param("currentPublishedAt") Instant currentPublishedAt,
            Pageable pageable
    );

    @Query("SELECT a FROM NewsArticle a WHERE a.isActive = true AND " +
            "((a.status = 'PUBLISHED' AND a.publishedAt <= :now) OR " +
            " (a.status = 'SCHEDULED' AND a.publishedAt <= :now)) AND " +
            "a.id != :currentId AND a.publishedAt > :currentPublishedAt " +
            "ORDER BY a.publishedAt ASC")
    List<NewsArticle> findNextPublicArticle(
            @Param("now") Instant now,
            @Param("currentId") UUID currentId,
            @Param("currentPublishedAt") Instant currentPublishedAt,
            Pageable pageable
    );

    List<NewsArticle> findByIsActiveTrueAndStatusAndPublishedAtGreaterThanOrderByPublishedAtAsc(
            NewsStatus status,
            Instant now
    );

    @Query("SELECT a FROM NewsArticle a WHERE a.isActive = true AND a.status = 'PUBLISHED' AND a.featuredSlot = 'MAIN'")
    Optional<NewsArticle> findCurrentPublishedMain();

    @Query("SELECT a FROM NewsArticle a WHERE a.isActive = true AND a.status = 'PUBLISHED' AND a.featuredSlot = 'SECONDARY' ORDER BY a.publishedAt DESC")
    List<NewsArticle> findPublishedSecondary(Pageable pageable);

    @Query("SELECT a FROM NewsArticle a WHERE a.isActive = true AND a.status = 'PUBLISHED' AND a.id NOT IN :excludedIds ORDER BY a.publishedAt DESC")
    List<NewsArticle> findPublishedAvailable(@Param("excludedIds") Collection<UUID> excludedIds, Pageable pageable);

    @Modifying
    @Query("UPDATE NewsArticle a SET a.featuredSlot = 'NONE', a.isFeatured = false WHERE a.featuredSlot = 'MAIN' AND a.isActive = true AND a.id != :exceptId")
    void clearOtherMainSlots(@Param("exceptId") UUID exceptId);

    @Modifying
    @Query("UPDATE NewsArticle a SET a.featuredSlot = 'NONE', a.isFeatured = false WHERE a.featuredSlot = 'MAIN' AND a.isActive = true")
    void clearAllMainSlots();

    @Modifying
    @Query("UPDATE NewsArticle a SET a.featuredSlot = 'NONE', a.isFeatured = false WHERE a.featuredSlot IN ('MAIN', 'SECONDARY') AND a.isActive = true AND a.id NOT IN :keepIds")
    void clearSlotsExcept(@Param("keepIds") Collection<UUID> keepIds);

    @Modifying
    @Query("UPDATE NewsArticle a SET a.featuredSlot = 'SECONDARY', a.isFeatured = true WHERE a.id IN :ids AND a.isActive = true")
    void setSecondarySlots(@Param("ids") Collection<UUID> ids);

    @Modifying
    @Query("UPDATE NewsArticle a SET a.status = 'PUBLISHED' WHERE a.status = 'SCHEDULED' AND a.publishedAt <= :now AND a.isActive = true")
    int publishScheduledArticles(@Param("now") Instant now);

    @Modifying
    @Query("UPDATE NewsArticle a SET a.viewsCount = a.viewsCount + 1 WHERE a.id = :id")
    void incrementViewsCount(@Param("id") UUID id);
}

