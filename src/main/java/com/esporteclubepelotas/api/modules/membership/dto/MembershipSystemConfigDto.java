package com.esporteclubepelotas.api.modules.membership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipSystemConfigDto {
    private String provider; // ELEVENTICKETS ou NATIVE
    private String externalUrl;
}

