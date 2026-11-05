package com.esporteclubepelotas.api.modules.news.dto;

import com.esporteclubepelotas.api.modules.news.entity.NewsFeaturedSlot;
import com.esporteclubepelotas.api.modules.news.entity.NewsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateNewsDto {

    @NotBlank(message = "O título da matéria é obrigatório.")
    @Size(max = 200, message = "O título deve ter no máximo 200 caracteres.")
    @Schema(description = "Título da matéria", example = "Pelotas vence o clássico Farroupilha por 2 a 0")
    private String title;

    @Size(max = 300, message = "O subtítulo deve ter no máximo 300 caracteres.")
    @Schema(description = "Subtítulo / Linha fina da matéria", example = "Com gols no segundo tempo, o Lobo garantiu os três pontos na Boca do Lobo.")
    private String subtitle;

    @Schema(description = "URL da imagem de capa", example = "https://firebasestorage.../cover.jpg")
    private String coverImage;

    @Size(max = 150, message = "A legenda da capa deve ter no máximo 150 caracteres.")
    @Schema(description = "Legenda / Créditos da imagem de capa", example = "Foto: Assessoria / E.C. Pelotas")
    private String coverCaption;

    @NotNull(message = "O conteúdo da matéria é obrigatório.")
    @Schema(description = "Conteúdo em blocos Tiptap (JSON estruturado)")
    private Map<String, Object> content;

    @Size(max = 500, message = "O resumo deve ter no máximo 500 caracteres.")
    @Schema(description = "Resumo da matéria para SEO e cards", example = "Vitória expressiva em casa com grande apoio da torcida áureo-cerúlea.")
    private String summary;

    @NotBlank(message = "A categoria da matéria é obrigatória.")
    @Schema(description = "ID ou UUID da Categoria", example = "60d5ec49f1b2c8b1f8e4e1a1")
    private String category;

    @Schema(description = "Tags da matéria", example = "[\"Gauchão\", \"Lobo\", \"BocaDoLobo\"]")
    private List<String> tags;

    @Schema(description = "Status da matéria", example = "PUBLISHED")
    private NewsStatus status;

    @Schema(description = "Se a matéria deve ser destaque", example = "false")
    private Boolean isFeatured;

    @Schema(description = "Slot de destaque", example = "MAIN")
    private NewsFeaturedSlot featuredSlot;

    @Schema(description = "Fixar no topo do feed", example = "false")
    private Boolean isPinned;

    @Builder.Default
    @Schema(description = "Exibir na página principal (Home do portal)", example = "true")
    private Boolean showOnHomepage = true;

    @Builder.Default
    @Schema(description = "Indica se a matéria pertence às Categorias de Base (Hub da Base)", example = "false")
    private Boolean isYouth = false;

    @Size(max = 120, message = "O nome do repórter deve ter no máximo 120 caracteres.")
    @Schema(description = "Nome do repórter / autor do texto", example = "Gustavo Freitas / Assessoria FGF")
    private String reporterName;

    @Size(max = 120, message = "O nome do fotógrafo deve ter no máximo 120 caracteres.")
    @Schema(description = "Nome do fotógrafo / agência de fotos", example = "Lucas Silva / Foto Pelotas")
    private String photographerName;

    @Schema(description = "Data/Hora de publicação ou agendamento (ISO 8601)", example = "2026-11-03T12:00:00.000Z")
    private String publishedAt;

    @Schema(description = "Galeria de fotos (URLs)")
    private List<String> gallery;

    @Schema(description = "URL de vídeo incorporado do YouTube")
    private String youtubeVideoUrl;

    @Builder.Default
    @Schema(description = "Permitir reações dos torcedores", example = "true")
    private Boolean allowReactions = true;

    @Builder.Default
    @Schema(description = "Permitir comentários dos torcedores", example = "true")
    private Boolean allowComments = true;
}
