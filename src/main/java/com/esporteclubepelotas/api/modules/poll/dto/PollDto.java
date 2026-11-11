package com.esporteclubepelotas.api.modules.poll.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollDto {
    private UUID id;
    private String title;
    private String description;
    private boolean onlyMembers;
    private boolean active;
    private Instant startsAt;
    private Instant endsAt;
    private Integer totalVotes;
    private boolean userHasVoted;
    private UUID userVotedOptionId;
    private List<PollOptionDto> options;
    private Instant createdAt;
    private Instant updatedAt;
}

