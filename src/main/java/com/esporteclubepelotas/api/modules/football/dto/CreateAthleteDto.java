package com.esporteclubepelotas.api.modules.football.dto;

import com.esporteclubepelotas.api.modules.football.entity.PreferredFoot;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAthleteDto {

    @NotBlank(message = "O nome completo do atleta é obrigatório.")
    @Size(max = 150, message = "O nome do atleta deve ter no máximo 150 caracteres.")
    @Schema(description = "Nome completo do atleta", example = "Walter Henrique de Oliveira")
    private String name;

    @Size(max = 100, message = "O apelido deve ter no máximo 100 caracteres.")
    @Schema(description = "Apelido ou nome de camisa", example = "Walter")
    private String nickname;

    @Schema(description = "Slug único (gerado automaticamente se omitido)", example = "walter")
    private String slug;

    @Schema(description = "URL da foto oficial do atleta")
    private String photoUrl;

    @Schema(description = "Data de nascimento (ISO-8601)", example = "1989-07-22")
    private String birthDate;

    @Size(max = 150, message = "A naturalidade deve ter no máximo 150 caracteres.")
    @Schema(description = "Naturalidade (Cidade - UF)", example = "Recife - PE")
    private String birthPlace;

    @Schema(description = "Altura em centímetros", example = "178")
    private Integer height;

    @Schema(description = "Peso em kg", example = "85")
    private Integer weight;

    @Schema(description = "Pé preferencial", example = "RIGHT")
    private PreferredFoot preferredFoot;

    @NotBlank(message = "A posição principal é obrigatória.")
    @Size(max = 80, message = "A posição principal deve ter no máximo 80 caracteres.")
    @Schema(description = "Posição principal de atuação", example = "Centroavante")
    private String primaryPosition;

    @Schema(description = "Posições secundárias", example = "[\"Segundo Atacante\"]")
    private List<String> secondaryPositions;

    @Schema(description = "Clubes anteriores", example = "[\"Goiás\", \"Fluminense\", \"Athletico-PR\"]")
    private List<String> pastClubs;

    @Schema(description = "Resumo da carreira e características")
    private String biography;

    @Size(max = 100, message = "O instagram deve ter no máximo 100 caracteres.")
    @Schema(description = "Perfil no Instagram", example = "walter_oficial")
    private String instagram;

    @Builder.Default
    @Schema(description = "Status ativo do atleta", example = "true")
    private Boolean isActive = true;
}

