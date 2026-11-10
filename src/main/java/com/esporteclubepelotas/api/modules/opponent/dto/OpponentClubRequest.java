package com.esporteclubepelotas.api.modules.opponent.dto;

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
public class OpponentClubRequest {

    @NotBlank(message = "Nome do clube é obrigatório")
    @Size(min = 2, max = 150, message = "Nome do clube deve ter entre 2 e 150 caracteres")
    private String name;

    @Size(max = 60, message = "Nome reduzido / apelido deve ter no máximo 60 caracteres")
    private String shortName;

    private String badgeUrl;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String city;

    @Size(max = 150, message = "Nome do estádio deve ter no máximo 150 caracteres")
    private String stadiumName;

    private Boolean isActive;

    private Integer displayOrder;
}
