package com.esporteclubepelotas.api.modules.setting.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpeningHourItem implements Serializable {
    private String department;
    private String schedule;
}

