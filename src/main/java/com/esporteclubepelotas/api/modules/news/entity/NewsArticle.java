package com.esporteclubepelotas.api.modules.news.entity;

import com.esporteclubepelotas.api.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "news_articles", indexes = {
        @Index(name = "idx_news_articles_slug", columnList = "slug", unique = true),
        @Index(name = "idx_news_articles_feed", columnList = "is_active, status, is_pinned, published_at"),
        @Index(name = "idx_news_articles_featured", columnList = "is_active, status, featured_slot, published_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 300)
    private String subtitle;

    @Column(nullable = false, unique = true, length = 220)
    private String slug;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> previousSlugs = new ArrayList<>();

    @Column(nullable = false, length = 1000)
    private String coverImage;

    @Column(length = 150)
    private String coverCaption;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private Map<String, Object> content = new HashMap<>();

    @Column(length = 500)
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private NewsCategory category;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NewsStatus status = NewsStatus.DRAFT;

    @Column(nullable = false)
    @Builder.Default
    private boolean isFeatured = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NewsFeaturedSlot featuredSlot = NewsFeaturedSlot.NONE;

    @Column(nullable = false)
    @Builder.Default
    private boolean isPinned = false;

    @Column(length = 120)
    private String reporterName;

    @Column(length = 120)
    private String photographerName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    private Instant publishedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> gallery = new ArrayList<>();

    @Column(length = 500)
    private String youtubeVideoUrl;

    @Column(nullable = false)
    @Builder.Default
    private Long viewsCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private boolean allowReactions = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean allowComments = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private NewsReactionsCounter reactions = new NewsReactionsCounter();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_edited_by_id")
    private User lastEditedBy;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}

