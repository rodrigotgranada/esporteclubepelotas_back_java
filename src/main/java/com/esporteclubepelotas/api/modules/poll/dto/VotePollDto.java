package com.esporteclubepelotas.api.modules.poll.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotePollDto {

    @NotNull(message = "A opção escolhida é obrigatória.")
    private UUID optionId;
}

