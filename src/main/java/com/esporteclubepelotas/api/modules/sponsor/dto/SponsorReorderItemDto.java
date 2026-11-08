package com.esporteclubepelotas.api.modules.sponsor.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SponsorReorderItemDto {

    @NotNull(message = "O ID do patrocinador é obrigatório.")
    private UUID id;

    @NotNull(message = "A ordem de exibição é obrigatória.")
    @JsonProperty("displayOrder")
    @JsonAlias({"display_order", "order"})
    private Integer displayOrder;
}

