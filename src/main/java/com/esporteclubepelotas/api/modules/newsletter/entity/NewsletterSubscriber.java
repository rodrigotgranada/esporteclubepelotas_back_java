package com.esporteclubepelotas.api.modules.newsletter.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "newsletter_subscribers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsletterSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "unsubscribe_token", nullable = false, unique = true, length = 64)
    private String unsubscribeToken;

    @Column(name = "unsubscribe_reason", length = 255)
    private String unsubscribeReason;

    @Column(name = "unsubscribed_at")
    private Instant unsubscribedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void ensureUnsubscribeToken() {
        if (this.unsubscribeToken == null || this.unsubscribeToken.isBlank()) {
            this.unsubscribeToken = UUID.randomUUID().toString();
        }
    }
}
