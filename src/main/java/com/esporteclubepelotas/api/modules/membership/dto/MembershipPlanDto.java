package com.esporteclubepelotas.api.modules.membership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlanDto {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private String billingPeriod;
    private List<String> advantages;
    private String badgeText;
    private boolean highlighted;
    private Integer displayOrder;
    private String whatsappOverride;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
