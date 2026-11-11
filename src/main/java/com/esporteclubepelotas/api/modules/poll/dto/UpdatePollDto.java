package com.esporteclubepelotas.api.modules.poll.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePollDto {
    private String title;
    private String description;
    private Boolean onlyMembers;
    private Boolean active;
    private Instant startsAt;
    private Instant endsAt;
}

