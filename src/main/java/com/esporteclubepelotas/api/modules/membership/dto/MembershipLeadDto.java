package com.esporteclubepelotas.api.modules.membership.dto;

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
public class MembershipLeadDto {
    private UUID id;
    private UUID planId;
    private String planName;
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String cpf;
    private String status;
    private String notes;
    private Instant createdAt;
}
