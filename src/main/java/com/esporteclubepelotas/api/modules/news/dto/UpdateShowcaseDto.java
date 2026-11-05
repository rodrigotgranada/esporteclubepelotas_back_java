package com.esporteclubepelotas.api.modules.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShowcaseDto {

    @Schema(description = "ID da matéria no Slot 1 (Manchetão Principal) ou null para vazio")
    private String mainId;

    @Schema(description = "Lista de IDs das matérias nos Slots Secundários (máx 2)")
    private List<String> secondaryIds;
}

