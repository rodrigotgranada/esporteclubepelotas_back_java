package com.esporteclubepelotas.api.modules.membership.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "eleventickets_members_cache")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EleventicketsMemberCache {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, unique = true, length = 20)
    private String cpf;

    @Column(name = "eleventickets_id", nullable = false, length = 50)
    private String eleventicketsId;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(length = 150)
    private String email;

    @Column(name = "plan_name", length = 100)
    private String planName;

    @Column(length = 30)
    @Builder.Default
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "imported_at", updatable = false)
    private Instant importedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.cpf != null) {
            this.cpf = this.cpf.replaceAll("[^0-9]", "");
        }
    }
}

