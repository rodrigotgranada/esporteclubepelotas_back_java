package com.esporteclubepelotas.api.modules.theme.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "themes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = false;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @Builder.Default
    @Column(name = "is_campaign", nullable = false)
    private boolean isCampaign = false;

    @Column(name = "campaign_color")
    private String campaignColor;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "light_palette")
    private ThemePalette light;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dark_palette")
    private ThemePalette dark;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
