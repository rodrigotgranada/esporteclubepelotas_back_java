package com.esporteclubepelotas.api.modules.membership.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMembershipLeadStatusDto {

    @NotBlank(message = "O status é obrigatório")
    private String status; // NOVO, CONTATADO, CONVERTIDO, CANCELADO

    private String notes;
}
