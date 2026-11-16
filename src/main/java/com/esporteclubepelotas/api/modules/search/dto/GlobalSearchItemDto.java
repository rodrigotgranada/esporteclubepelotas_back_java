package com.esporteclubepelotas.api.modules.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSearchItemDto {
    private String id;
    private String slug;
    private String type; // NEWS | ATHLETE | IDOL | MATCH | TRANSPARENCY
    private String title;
    private String subtitle;
    private String badge;
    private String imageUrl;
    private String url;
    private String externalUrl;
}

