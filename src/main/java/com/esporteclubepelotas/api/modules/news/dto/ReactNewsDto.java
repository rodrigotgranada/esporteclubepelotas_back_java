package com.esporteclubepelotas.api.modules.news.dto;

import com.esporteclubepelotas.api.modules.news.entity.NewsReactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactNewsDto {

    @NotNull(message = "O tipo da reação é obrigatório (wolf, fire, clap, heart).")
    @Schema(description = "Tipo da reação", example = "wolf")
    private NewsReactionType type;
}

