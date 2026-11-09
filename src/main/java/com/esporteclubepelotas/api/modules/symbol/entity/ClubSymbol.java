package com.esporteclubepelotas.api.modules.symbol.entity;

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
@Table(name = "club_symbols")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubSymbol {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SymbolCategory category;

    @Column(length = 150)
    private String name;

    @Column(length = 250)
    private String subtitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "gallery_urls", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> galleryUrls = new ArrayList<>();

    @Column(name = "year_start")
    private Integer yearStart;

    @Column(name = "year_end")
    private Integer yearEnd;

    @Column(name = "is_official", nullable = false)
    @Builder.Default
    private Boolean isOfficial = false;

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

