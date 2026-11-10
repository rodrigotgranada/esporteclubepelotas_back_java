package com.esporteclubepelotas.api.modules.match.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "matches", indexes = {
        @Index(name = "idx_matches_championship_date", columnList = "championship_id, match_date"),
        @Index(name = "idx_matches_status_date", columnList = "status, match_date")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "championship_id", nullable = false)
    private Championship championship;

    @Column(name = "opponent_name", nullable = false, length = 160)
    private String opponentName;

    @Column(name = "opponent_logo_url", columnDefinition = "TEXT")
    private String opponentLogoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "home_away", nullable = false, length = 10)
    @Builder.Default
    private HomeAway homeAway = HomeAway.HOME;

    @Column(name = "stadium_name", nullable = false, length = 160)
    private String stadiumName;

    @Column(length = 120)
    private String city;

    @Column(name = "match_date", nullable = false)
    private Instant matchDate;

    @Column(name = "round_name", nullable = false, length = 100)
    private String roundName;

    @Column(length = 255)
    private String slug;

    @Column(name = "has_live_coverage", nullable = false)
    @Builder.Default
    private Boolean hasLiveCoverage = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MatchStatus status = MatchStatus.SCHEDULED;

    @Column(name = "pelotas_score")
    private Integer pelotasScore;

    @Column(name = "opponent_score")
    private Integer opponentScore;

    @Column(name = "penalty_pelotas_score")
    private Integer penaltyPelotasScore;

    @Column(name = "penalty_opponent_score")
    private Integer penaltyOpponentScore;

    @Column(name = "ticket_url", columnDefinition = "TEXT")
    private String ticketUrl;

    @Column(name = "broadcast_channel", length = 200)
    private String broadcastChannel;

    @Column(name = "transmission_url", columnDefinition = "TEXT")
    private String transmissionUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "current_period", length = 30)
    @Builder.Default
    private String currentPeriod = "PRE_MATCH";

    @Column(name = "current_minute", length = 20)
    @Builder.Default
    private String currentMinute = "0'";

    @Column(name = "timer_started_at")
    private Instant timerStartedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}

