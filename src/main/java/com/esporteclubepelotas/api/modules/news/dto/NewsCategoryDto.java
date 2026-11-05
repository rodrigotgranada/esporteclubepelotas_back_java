package com.esporteclubepelotas.api.modules.news.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsCategoryDto {

    private String id;

    @JsonProperty("_id")
    private String mongoId;

    private String name;
    private String slug;
    private String description;
    private boolean isActive;

    @JsonProperty("order")
    private int order;

    private Instant createdAt;
    private Instant updatedAt;
}

