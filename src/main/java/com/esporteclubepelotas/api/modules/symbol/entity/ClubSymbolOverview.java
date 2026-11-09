package com.esporteclubepelotas.api.modules.symbol.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "club_symbols_overview")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubSymbolOverview {

    @Id
    @Column(name = "category", length = 30, nullable = false)
    private String category; // MASCOT, SHIELD, FLAG_COLORS

    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
