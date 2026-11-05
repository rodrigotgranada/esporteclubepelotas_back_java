package com.esporteclubepelotas.api.modules.football.entity;

import com.esporteclubepelotas.api.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
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
@Table(name = "athletes", indexes = {
        @Index(name = "idx_athletes_slug", columnList = "slug", unique = true),
        @Index(name = "idx_athletes_active_name", columnList = "is_active, name"),
        @Index(name = "idx_athletes_position", columnList = "primary_position"),
        @Index(name = "idx_athletes_gender_type", columnList = "is_active, gender, athlete_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 100)
    private String nickname;

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Column(length = 1000)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AthleteGender gender = AthleteGender.MALE;

    @Enumerated(EnumType.STRING)
    @Column(name = "athlete_type", nullable = false, length = 20)
    @Builder.Default
    private AthleteType athleteType = AthleteType.PROFESSIONAL;

    @Column(name = "youth_category", length = 30)
    private String youthCategory;

    @Column(name = "entry_year")
    private Integer entryYear;

    private LocalDate birthDate;

    @Column(length = 150)
    private String birthPlace;

    private Integer height; // em centímetros (ex: 182)

    private Integer weight; // em kg (ex: 78)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PreferredFoot preferredFoot = PreferredFoot.RIGHT;

    @Column(name = "primary_position", nullable = false, length = 80)
    private String primaryPosition;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> secondaryPositions = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<String> pastClubs = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(length = 100)
    private String instagram;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
