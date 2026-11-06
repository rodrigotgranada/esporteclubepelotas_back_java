package com.esporteclubepelotas.api.modules.football.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "youth_portal_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YouthPortalSetting {

    @Id
    private Long id;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pillars", nullable = false)
    private List<YouthPillarItem> pillars = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "peneiras_criteria", nullable = false)
    private List<String> peneirasCriteria = new ArrayList<>();

    @Builder.Default
    @Column(name = "contact_title", length = 100)
    private String contactTitle = "Departamento de Futebol de Base";

    @Column(name = "contact_description", length = 500)
    private String contactDescription;

    @Column(name = "contact_email", length = 150)
    private String contactEmail;

    @Column(name = "contact_phone", length = 80)
    private String contactPhone;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
