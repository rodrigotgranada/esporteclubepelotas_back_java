package com.esporteclubepelotas.api.modules.football.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YouthPortalConfigDto {

    @Valid
    @Builder.Default
    @Size(max = 4, message = "É permitido cadastrar no máximo 4 pilares do futebol de base.")
    private List<YouthPillarDto> pillars = new ArrayList<>();

    @Builder.Default
    private List<String> peneirasCriteria = new ArrayList<>();

    @Size(max = 100, message = "O título de contato deve ter no máximo 100 caracteres.")
    private String contactTitle;

    @Size(max = 500, message = "A descrição de contato deve ter no máximo 500 caracteres.")
    private String contactDescription;

    @Size(max = 150, message = "O e-mail de contato deve ter no máximo 150 caracteres.")
    private String contactEmail;

    @Size(max = 80, message = "O telefone/WhatsApp de contato deve ter no máximo 80 caracteres.")
    private String contactPhone;
}
