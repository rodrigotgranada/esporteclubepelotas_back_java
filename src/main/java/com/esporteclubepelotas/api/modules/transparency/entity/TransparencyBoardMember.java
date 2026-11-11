package com.esporteclubepelotas.api.modules.transparency.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transparency_board_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransparencyBoardMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "role_title", nullable = false, length = 150)
    private String roleTitle;

    @Column(name = "board_type", nullable = false, length = 100)
    private String boardType; // 'DIRETORIA_EXECUTIVA', 'CONSELHO_DELIBERATIVO', 'CONSELHO_FISCAL'

    @Column(name = "term_period", length = 100)
    private String termPeriod;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

