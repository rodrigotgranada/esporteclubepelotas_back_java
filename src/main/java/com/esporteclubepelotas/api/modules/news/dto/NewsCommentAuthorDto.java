package com.esporteclubepelotas.api.modules.news.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsCommentAuthorDto {

    private String id;

    @JsonProperty("_id")
    private String mongoId;

    private String firstName;
    private String lastName;
    private String email;
    private String avatar;
    private String avatarUrl;
    private String role;
}

