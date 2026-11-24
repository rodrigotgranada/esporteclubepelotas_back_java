package com.esporteclubepelotas.api.modules.integrations.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorePortalUserDto {
    private String portalUserId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String cpf;
    private String role;
    private boolean isSocioActive;
    private String socioPlanName;
    private String socioStatus;
}
