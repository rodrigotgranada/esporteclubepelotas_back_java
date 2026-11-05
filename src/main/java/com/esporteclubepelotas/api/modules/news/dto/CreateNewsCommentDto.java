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
public class CreateNewsCommentDto {

    @NotBlank(message = "O comentário não pode estar vazio.")
    @Size(min = 1, max = 1000, message = "O comentário deve ter entre 1 e 1000 caracteres.")
    @Schema(description = "Conteúdo do comentário do torcedor", example = "Vamos Pelotas! Que vitória maravilhosa na Boca do Lobo!")
    private String content;
}

