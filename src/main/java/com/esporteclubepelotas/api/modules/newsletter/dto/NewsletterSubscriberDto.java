package com.esporteclubepelotas.api.modules.newsletter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterSubscriberDto {
    private UUID id;
    private String email;
    private String name;
    private boolean active;
    private Instant createdAt;
}

