package com.esporteclubepelotas.api.modules.setting.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactEmailItem implements Serializable {
    private String label;
    private String email;
}
