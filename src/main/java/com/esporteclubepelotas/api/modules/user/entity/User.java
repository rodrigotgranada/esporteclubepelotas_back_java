package com.esporteclubepelotas.api.modules.user.entity;

import com.esporteclubepelotas.api.modules.role.entity.Role;
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
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    @Builder.Default
    private boolean mustChangePassword = false;

    @Column(nullable = false, unique = true, length = 20)
    private String cpf;

    private LocalDate birthDate;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String avatarUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private int failedLoginAttempts = 0;

    private Instant lockoutUntil;

    @Column(length = 100)
    private String resetPasswordToken;

    private Instant resetPasswordExpires;

    @Column(length = 10)
    private String verificationCode;

    private Instant verificationCodeExpires;

    @Column(name = "token_version", nullable = false, columnDefinition = "integer default 1")
    @Builder.Default
    private int tokenVersion = 1;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<PhoneItem> phones = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<AddressItem> addresses = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private UserPreferences preferences = new UserPreferences();

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private List<PendingChangeItem> pendingChanges = new ArrayList<>();

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
        if (this.cpf != null) {
            this.cpf = this.cpf.replaceAll("[^0-9]", "");
        }
        if (this.phones == null) {
            this.phones = new ArrayList<>();
        }
        if (this.addresses == null) {
            this.addresses = new ArrayList<>();
        }
        if (this.preferences == null) {
            this.preferences = new UserPreferences();
        }
        if (this.pendingChanges == null) {
            this.pendingChanges = new ArrayList<>();
        }
    }
}
