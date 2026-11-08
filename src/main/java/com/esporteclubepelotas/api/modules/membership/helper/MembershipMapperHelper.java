package com.esporteclubepelotas.api.modules.membership.helper;

import com.esporteclubepelotas.api.modules.membership.dto.MembershipLeadDto;
import com.esporteclubepelotas.api.modules.membership.dto.MembershipPlanDto;
import com.esporteclubepelotas.api.modules.membership.entity.MembershipLead;
import com.esporteclubepelotas.api.modules.membership.entity.MembershipPlan;

import java.util.ArrayList;

public final class MembershipMapperHelper {

    private MembershipMapperHelper() {}

    public static MembershipPlanDto toDto(MembershipPlan plan) {
        if (plan == null) return null;

        return MembershipPlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .slug(plan.getSlug())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .billingPeriod(plan.getBillingPeriod())
                .advantages(plan.getAdvantages() != null ? new ArrayList<>(plan.getAdvantages()) : new ArrayList<>())
                .badgeText(plan.getBadgeText())
                .highlighted(plan.isHighlighted())
                .displayOrder(plan.getDisplayOrder() != null ? plan.getDisplayOrder() : 0)
                .whatsappOverride(plan.getWhatsappOverride())
                .active(plan.isActive())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }

    public static MembershipLeadDto toLeadDto(MembershipLead lead) {
        if (lead == null) return null;

        return MembershipLeadDto.builder()
                .id(lead.getId())
                .planId(lead.getPlan() != null ? lead.getPlan().getId() : null)
                .planName(lead.getPlan() != null ? lead.getPlan().getName() : null)
                .userId(lead.getUser() != null ? lead.getUser().getId() : null)
                .name(lead.getName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .cpf(lead.getCpf())
                .status(lead.getStatus())
                .notes(lead.getNotes())
                .createdAt(lead.getCreatedAt())
                .build();
    }
}
