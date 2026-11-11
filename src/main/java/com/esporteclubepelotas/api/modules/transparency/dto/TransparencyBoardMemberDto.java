package com.esporteclubepelotas.api.modules.transparency.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransparencyBoardMemberDto {
    private UUID id;
    private String name;
    private String roleTitle;
    private String boardType;
    private String termPeriod;
    private String photoUrl;
    private Integer displayOrder;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}

