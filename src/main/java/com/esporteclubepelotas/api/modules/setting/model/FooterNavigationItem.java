package com.esporteclubepelotas.api.modules.setting.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FooterNavigationItem implements Serializable {
    private String label;
    private String url;

    @JsonProperty("isVisible")
    private Boolean isVisible;

    @JsonProperty("isExternal")
    private Boolean isExternal;
}
