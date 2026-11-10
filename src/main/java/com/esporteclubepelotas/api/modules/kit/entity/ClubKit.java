package com.esporteclubepelotas.api.modules.kit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "club_kits", indexes = {
        @Index(name = "idx_club_kits_current", columnList = "is_current"),
        @Index(name = "idx_club_kits_season_year", columnList = "season_year"),
        @Index(name = "idx_club_kits_competition", columnList = "competition"),
        @Index(name = "idx_club_kits_type", columnList = "kit_type"),
        @Index(name = "idx_club_kits_active", columnList = "is_active"),
        @Index(name = "idx_club_kits_deleted", columnList = "deleted_at"),
        @Index(name = "idx_club_kits_order", columnList = "display_order")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubKit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "kit_type", nullable = false, length = 30)
    private KitType kitType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "championship_id")
    private com.esporteclubepelotas.api.modules.match.entity.Championship championship;

    @Column(nullable = false, length = 150)
    private String competition;

    @Column(name = "season_year", nullable = false)
    private Integer seasonYear;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "gallery_urls", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> galleryUrls = new ArrayList<>();

    @Column(length = 100)
    private String manufacturer;

    @Column(length = 150)
    private String sponsor;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_current", nullable = false)
    @Builder.Default
    private Boolean isCurrent = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}

