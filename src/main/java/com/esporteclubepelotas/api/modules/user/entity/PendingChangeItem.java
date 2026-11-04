package com.esporteclubepelotas.api.modules.user.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PendingChangeItem implements Serializable {
    private String type; // "email" or "phone"
    private String newValue;
    private String code;
    private Instant expiresAt;
}

