package com.esporteclubepelotas.api.modules.membership.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMembershipPlanDto {

    @Size(max = 150, message = "O nome não pode exceder 150 caracteres")
    private String name;

    private String slug;

    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "O valor não pode ser negativo")
    private BigDecimal price;

    private String billingPeriod;

    private List<String> advantages;

    private String badgeText;

    private Boolean highlighted;

    private Integer displayOrder;

    private String whatsappOverride;

    private Boolean active;
}

