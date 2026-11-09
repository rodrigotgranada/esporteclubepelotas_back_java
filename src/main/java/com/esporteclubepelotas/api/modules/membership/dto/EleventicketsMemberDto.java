package com.esporteclubepelotas.api.modules.membership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EleventicketsMemberDto {

    private String userId;
    private String name;
    private String email;
    private String cpf;
    private String avatarUrl;
    private String roleName;
    private String eleventicketsId;
    private boolean isSocio;
    private String planName;
    private String status;
    private String syncMode; // AUTO ou MANUAL_OVERRIDE
    private String overrideReason;
    private Instant overrideAt;
    private LocalDate socioSince;
    private Instant lastSyncedAt;
    private Instant createdAt;
}

