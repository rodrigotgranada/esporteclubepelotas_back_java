package com.esporteclubepelotas.api.modules.news.dto;

import com.esporteclubepelotas.api.modules.news.entity.NewsFeaturedSlot;
import com.esporteclubepelotas.api.modules.news.entity.NewsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryNewsDto {

    @Builder.Default
    @Schema(description = "Número da página", defaultValue = "1")
    private Integer page = 1;

    @Builder.Default
    @Schema(description = "Limite por página", defaultValue = "10")
    private Integer limit = 10;

    @Schema(description = "Termo de busca no título, subtítulo ou resumo")
    private String search;

    @Schema(description = "ID, UUID ou Slug da Categoria")
    private String category;

    @Schema(description = "Filtrar por tag específica")
    private String tag;

    @Schema(description = "Status da matéria")
    private NewsStatus status;

    @Schema(description = "Filtrar por matérias em destaque")
    private Boolean isFeatured;

    @Schema(description = "Filtrar por slot de destaque")
    private NewsFeaturedSlot featuredSlot;

    @Schema(description = "Filtrar por matérias fixadas no topo")
    private Boolean isPinned;
}

