package com.esporteclubepelotas.api.modules.football.dto;

import com.esporteclubepelotas.api.modules.football.entity.PreferredFoot;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class UpdateAthleteDto {

    @Size(max = 150, message = "O nome do atleta deve ter no máximo 150 caracteres.")
    @Schema(description = "Nome completo do atleta")
    private String name;

    @Size(max = 100, message = "O apelido deve ter no máximo 100 caracteres.")
    @Schema(description = "Apelido ou nome de camisa")
    private String nickname;

    @Schema(description = "Slug personalizado")
    private String slug;

    @Schema(description = "URL da foto oficial do atleta")
    private String photoUrl;

    @Schema(description = "Data de nascimento (ISO-8601)")
    private String birthDate;

    @Size(max = 150, message = "A naturalidade deve ter no máximo 150 caracteres.")
    @Schema(description = "Naturalidade (Cidade - UF)")
    private String birthPlace;

    @Schema(description = "Altura em centímetros")
    private Integer height;

    @Schema(description = "Peso em kg")
    private Integer weight;

    @Schema(description = "Pé preferencial")
    private PreferredFoot preferredFoot;

    @Size(max = 80, message = "A posição principal deve ter no máximo 80 caracteres.")
    @Schema(description = "Posição principal de atuação")
    private String primaryPosition;

    @Schema(description = "Posições secundárias")
    private List<String> secondaryPositions;

    @Schema(description = "Clubes anteriores")
    private List<String> pastClubs;

    @Schema(description = "Resumo da carreira e características")
    private String biography;

    @Size(max = 100, message = "O instagram deve ter no máximo 100 caracteres.")
    @Schema(description = "Perfil no Instagram")
    private String instagram;

    @Schema(description = "Status ativo do atleta")
    private Boolean isActive;
}

