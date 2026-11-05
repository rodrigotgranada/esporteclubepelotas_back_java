package com.esporteclubepelotas.api.modules.news.dto;

import com.esporteclubepelotas.api.modules.news.entity.NewsFeaturedSlot;
import com.esporteclubepelotas.api.modules.news.entity.NewsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNewsDto {

    @Size(max = 200, message = "O título deve ter no máximo 200 caracteres.")
    @Schema(description = "Título da matéria")
    private String title;

    @Size(max = 300, message = "O subtítulo deve ter no máximo 300 caracteres.")
    @Schema(description = "Subtítulo / Linha fina da matéria")
    private String subtitle;

    @Schema(description = "Slug personalizado")
    private String slug;

    @Schema(description = "URL da imagem de capa")
    private String coverImage;

    @Size(max = 150, message = "A legenda da capa deve ter no máximo 150 caracteres.")
    @Schema(description = "Legenda / Créditos da imagem de capa")
    private String coverCaption;

    @Schema(description = "Conteúdo em blocos Tiptap (JSON estruturado)")
    private Map<String, Object> content;

    @Size(max = 500, message = "O resumo deve ter no máximo 500 caracteres.")
    @Schema(description = "Resumo da matéria para SEO e cards")
    private String summary;

    @Schema(description = "ID ou UUID da Categoria")
    private String category;

    @Schema(description = "Tags da matéria")
    private List<String> tags;

    @Schema(description = "Status da matéria")
    private NewsStatus status;

    @Schema(description = "Se a matéria deve ser destaque")
    private Boolean isFeatured;

    @Schema(description = "Slot de destaque")
    private NewsFeaturedSlot featuredSlot;

    @Schema(description = "Fixar no topo do feed")
    private Boolean isPinned;

    @Size(max = 120, message = "O nome do repórter deve ter no máximo 120 caracteres.")
    @Schema(description = "Nome do repórter / autor do texto")
    private String reporterName;

    @Size(max = 120, message = "O nome do fotógrafo deve ter no máximo 120 caracteres.")
    @Schema(description = "Nome do fotógrafo / agência de fotos")
    private String photographerName;

    @Schema(description = "Data/Hora de publicação ou agendamento (ISO 8601)")
    private String publishedAt;

    @Schema(description = "Galeria de fotos (URLs)")
    private List<String> gallery;

    @Schema(description = "URL de vídeo incorporado do YouTube")
    private String youtubeVideoUrl;

    @Schema(description = "Permitir reações dos torcedores")
    private Boolean allowReactions;

    @Schema(description = "Permitir comentários dos torcedores")
    private Boolean allowComments;
}

