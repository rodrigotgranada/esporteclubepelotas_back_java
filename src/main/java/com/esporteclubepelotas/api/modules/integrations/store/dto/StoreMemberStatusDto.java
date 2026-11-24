package com.esporteclubepelotas.api.modules.integrations.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreMemberStatusDto {
    private boolean found;
    private String portalUserId;
    private String name;
    private boolean isSocioActive;
    private String socioPlanName;
    private String socioStatus;
}
