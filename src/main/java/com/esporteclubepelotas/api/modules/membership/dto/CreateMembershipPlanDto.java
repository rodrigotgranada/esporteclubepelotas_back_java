package com.esporteclubepelotas.api.modules.membership.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateMembershipPlanDto {

    @NotBlank(message = "O nome do plano é obrigatório")
    @Size(max = 150, message = "O nome não pode exceder 150 caracteres")
    private String name;

    private String slug;

    private String description;

    @NotNull(message = "O valor do plano é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "O valor não pode ser negativo")
    private BigDecimal price;

    private String billingPeriod; // MENSAL, TRIMESTRAL, SEMESTRAL, ANUAL

    private List<String> advantages;

    private String badgeText;

    private Boolean highlighted;

    private Integer displayOrder;

    private String whatsappOverride;

    private Boolean active;
}

