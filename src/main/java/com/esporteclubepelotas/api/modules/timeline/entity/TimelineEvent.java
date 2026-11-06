package com.esporteclubepelotas.api.modules.timeline.entity;

import com.esporteclubepelotas.api.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "timeline_events", indexes = {
        @Index(name = "idx_timeline_year", columnList = "\"year\""),
        @Index(name = "idx_timeline_active_year", columnList = "is_active, \"year\"")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimelineEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "\"year\"", nullable = false)
    private Integer year;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private TimelineCategory category = TimelineCategory.MILESTONE;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "date_formatted", length = 100)
    private String dateFormatted;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_highlighted", nullable = false)
    @Builder.Default
    private boolean isHighlighted = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
