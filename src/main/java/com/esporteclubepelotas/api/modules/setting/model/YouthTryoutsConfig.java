package com.esporteclubepelotas.api.modules.setting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YouthTryoutsConfig {

    @Builder.Default
    private List<String> requirements = new ArrayList<>();

    private String contactEmail;
    private String contactPhone;
    private String coordinatorName;
}

