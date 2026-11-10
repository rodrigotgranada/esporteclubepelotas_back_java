package com.esporteclubepelotas.api.modules.match.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "match_events", indexes = {
        @Index(name = "idx_match_events_match_order", columnList = "match_id, display_order, minute, created_at"),
        @Index(name = "idx_match_events_match_period", columnList = "match_id, period")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(nullable = false)
    @Builder.Default
    private Integer minute = 0;

    @Column(name = "minute_extra")
    private Integer minuteExtra;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private MatchPeriod period = MatchPeriod.FIRST_HALF;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private MatchEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MatchEventTeam team = MatchEventTeam.NEUTRAL;

    @Column(name = "player_name", length = 150)
    private String playerName;

    @Column(name = "player_out_name", length = 150)
    private String playerOutName;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "score_pelotas_moment")
    private Integer scorePelotasMoment;

    @Column(name = "score_opponent_moment")
    private Integer scoreOpponentMoment;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

