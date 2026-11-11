package com.esporteclubepelotas.api.modules.poll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePollDto {

    @NotBlank(message = "O título da enquete é obrigatório.")
    private String title;

    private String description;

    @Builder.Default
    private boolean onlyMembers = true;

    @Builder.Default
    private boolean active = true;

    private Instant startsAt;
    private Instant endsAt;

    @NotEmpty(message = "A enquete deve possuir pelo menos 2 opções de voto.")
    private List<String> options;
}

