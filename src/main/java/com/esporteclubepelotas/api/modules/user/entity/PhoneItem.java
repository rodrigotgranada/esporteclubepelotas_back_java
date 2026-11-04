package com.esporteclubepelotas.api.modules.user.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhoneItem implements Serializable {
    private String number;
    @Builder.Default
    private boolean isWhatsapp = false;
    @Builder.Default
    private boolean isPrimary = false;
    @Builder.Default
    private boolean isVerified = false;
}

