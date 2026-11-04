package com.esporteclubepelotas.api.modules.setting.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLinkItem implements Serializable {
    private String platform;
    private String label;
    private String url;
}

