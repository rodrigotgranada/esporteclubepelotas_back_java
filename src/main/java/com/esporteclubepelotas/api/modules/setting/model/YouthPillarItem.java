package com.esporteclubepelotas.api.modules.setting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YouthPillarItem {
    private String title;
    private String description;
}

