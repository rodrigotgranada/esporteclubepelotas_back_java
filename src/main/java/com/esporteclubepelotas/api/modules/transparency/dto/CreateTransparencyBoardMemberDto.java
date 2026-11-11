package com.esporteclubepelotas.api.modules.transparency.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransparencyBoardMemberDto {

    @NotBlank(message = "O nome do membro é obrigatório.")
    private String name;

    @NotBlank(message = "O cargo/função é obrigatório.")
    private String roleTitle;

    @NotBlank(message = "O conselho/diretoria é obrigatório.")
    private String boardType; // 'DIRETORIA_EXECUTIVA', 'CONSELHO_DELIBERATIVO', 'CONSELHO_FISCAL'

    private String termPeriod;
    private String photoUrl;

    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private boolean active = true;
}

