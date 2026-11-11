package com.esporteclubepelotas.api.modules.poll.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollOptionDto {
    private UUID id;
    private String title;
    private Integer votesCount;
    private Integer displayOrder;
    private Double percentage; // Calculado dinamicamente
}

