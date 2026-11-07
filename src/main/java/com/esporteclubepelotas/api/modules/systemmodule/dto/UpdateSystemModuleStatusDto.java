package com.esporteclubepelotas.api.modules.systemmodule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSystemModuleStatusDto {

    @JsonProperty("isActive")
    private Boolean isActive;
}

