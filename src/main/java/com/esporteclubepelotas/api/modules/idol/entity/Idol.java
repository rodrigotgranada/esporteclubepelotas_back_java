package com.esporteclubepelotas.api.modules.idol.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "idols")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Idol {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 100)
    private String nickname;

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private IdolCategory category = IdolCategory.PLAYER;

    @Column(length = 500)
    private String photoUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> gallery = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<IdolGalleryItem> galleryItems = new ArrayList<>();

    @Column(nullable = false)
    private Integer startYear;

    private Integer endYear;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<IdolPeriod> periods = new ArrayList<>();

    @Builder.Default
    private boolean isActiveInClub = false;

    private LocalDate birthDate;

    private LocalDate deathDate;

    @Column(length = 100)
    private String birthPlace;

    @Column(length = 500)
    private String quote;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String biography;

    @JdbcTypeCode(SqlTypes.JSON)
    private IdolPlayerStats playerStats;

    @JdbcTypeCode(SqlTypes.JSON)
    private IdolCoachStats coachStats;

    @JdbcTypeCode(SqlTypes.JSON)
    private IdolStaffStats staffStats;

    @JdbcTypeCode(SqlTypes.JSON)
    private IdolFanStats fanStats;

    @Builder.Default
    private boolean isFeatured = false;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private int displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(length = 36)
    private String createdBy;

    @Column(length = 36)
    private String lastEditedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.gallery == null) {
            this.gallery = new ArrayList<>();
        }
        if (this.galleryItems == null) {
            this.galleryItems = new ArrayList<>();
        }
        if (this.periods == null) {
            this.periods = new ArrayList<>();
        }
    }
}

