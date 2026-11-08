package com.esporteclubepelotas.api.modules.membership.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReorderMembershipPlansDto {

    @NotEmpty(message = "A lista ordenada de IDs não pode ser vazia")
    private List<UUID> planIds;
}
