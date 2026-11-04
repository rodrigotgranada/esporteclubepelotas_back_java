package com.esporteclubepelotas.api.modules.setting.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactPhoneItem implements Serializable {
    private String label;
    private String number;

    @JsonProperty("isWhatsapp")
    private Boolean isWhatsapp;
}

