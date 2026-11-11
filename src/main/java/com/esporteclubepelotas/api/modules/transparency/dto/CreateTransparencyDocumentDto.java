package com.esporteclubepelotas.api.modules.transparency.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransparencyDocumentDto {

    @NotBlank(message = "O título do documento é obrigatório.")
    private String title;

    private String description;

    @NotBlank(message = "A categoria é obrigatória.")
    private String category; // 'ESTATUTO', 'BALANCO', 'ATA', 'FINANCEIRO', 'OUTRO'

    @NotNull(message = "O ano de referência é obrigatório.")
    private Integer year;

    @NotBlank(message = "A URL do arquivo PDF é obrigatória.")
    private String fileUrl;

    private String fileSize;

    @Builder.Default
    private boolean published = true;
}

