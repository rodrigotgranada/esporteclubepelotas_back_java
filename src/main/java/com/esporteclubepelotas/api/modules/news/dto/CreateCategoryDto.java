package com.esporteclubepelotas.api.modules.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryDto {

    @NotBlank(message = "O nome da categoria é obrigatório.")
    @Size(max = 80, message = "O nome da categoria deve ter no máximo 80 caracteres.")
    @Schema(description = "Nome da categoria", example = "Futebol Profissional")
    private String name;

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
    @Schema(description = "Descrição da categoria", example = "Notícias do time principal e jogos")
    private String description;
}

