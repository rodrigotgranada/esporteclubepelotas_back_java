package com.esporteclubepelotas.api.modules.news.controller;

import com.esporteclubepelotas.api.common.security.CurrentUser;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.news.dto.CreateCategoryDto;
import com.esporteclubepelotas.api.modules.news.dto.CreateNewsDto;
import com.esporteclubepelotas.api.modules.news.dto.GetNewsCommentsResponseDto;
import com.esporteclubepelotas.api.modules.news.dto.GetNewsResponseDto;
import com.esporteclubepelotas.api.modules.news.dto.NewsArticleDto;
import com.esporteclubepelotas.api.modules.news.dto.NewsCategoryDto;
import com.esporteclubepelotas.api.modules.news.dto.NewsCommentDto;
import com.esporteclubepelotas.api.modules.news.dto.QueryNewsDto;
import com.esporteclubepelotas.api.modules.news.dto.ShowcaseResponseDto;
import com.esporteclubepelotas.api.modules.news.dto.UpdateNewsDto;
import com.esporteclubepelotas.api.modules.news.dto.UpdateShowcaseDto;
import com.esporteclubepelotas.api.modules.news.entity.NewsCommentStatus;
import com.esporteclubepelotas.api.modules.news.service.NewsAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/news/admin")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('news.manage') or hasRole('ADMIN') or hasRole('OWNER') or hasRole('EDITOR')")
@Tag(name = "News (Administração / Editor)", description = "Gestão de matérias, categorias, vitrine da capa e moderação de comentários")
public class NewsAdminController {

    private final NewsAdminService newsAdminService;

    public NewsAdminController(NewsAdminService newsAdminService) {
        this.newsAdminService = newsAdminService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as matérias para gestão (inclui rascunhos e agendadas)")
    public ResponseEntity<GetNewsResponseDto> adminFindAll(@ModelAttribute QueryNewsDto query) {
        return ResponseEntity.ok(newsAdminService.adminFindAll(query));
    }

    @GetMapping("/categories")
    @Operation(summary = "Listar todas as categorias para seleção no editor")
    public ResponseEntity<List<NewsCategoryDto>> adminFindAllCategories() {
        return ResponseEntity.ok(newsAdminService.adminFindAllCategories());
    }

    @PostMapping("/categories")
    @Operation(summary = "Criar uma nova categoria in-line diretamente pelo editor")
    public ResponseEntity<NewsCategoryDto> createCategory(@RequestBody @Valid CreateCategoryDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsAdminService.createCategory(dto));
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de imagem para o corpo do texto da matéria")
    public ResponseEntity<Map<String, String>> uploadContentImage(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        MultipartFile targetFile = file != null ? file : image;
        if (targetFile == null || targetFile.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de imagem não informado.");
        }
        return ResponseEntity.ok(newsAdminService.uploadContentImage(targetFile));
    }

    @PostMapping(value = "/upload-cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload prévio da imagem de capa antes da persistência da matéria")
    public ResponseEntity<Map<String, String>> uploadStandaloneCover(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "cover", required = false) MultipartFile cover,
            @RequestParam(value = "folderId", required = false) String folderId
    ) {
        MultipartFile targetFile = file != null ? file : cover;
        if (targetFile == null || targetFile.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de capa não informado.");
        }
        return ResponseEntity.ok(newsAdminService.uploadStandaloneCover(targetFile, folderId));
    }

    @PostMapping(value = "/upload-gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload prévio de fotos da galeria antes da persistência da matéria")
    public ResponseEntity<Map<String, List<String>>> uploadStandaloneGallery(
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "gallery", required = false) List<MultipartFile> gallery,
            @RequestParam(value = "file", required = false) List<MultipartFile> singleFiles,
            @RequestParam(value = "folderId", required = false) String folderId
    ) {
        List<MultipartFile> targetFiles = files != null && !files.isEmpty() ? files
                : (gallery != null && !gallery.isEmpty() ? gallery : singleFiles);
        if (targetFiles == null || targetFiles.isEmpty()) {
            throw new IllegalArgumentException("Nenhum arquivo enviado para a galeria.");
        }
        return ResponseEntity.ok(newsAdminService.uploadStandaloneGallery(targetFiles, folderId));
    }

    @GetMapping("/comments")
    @Operation(summary = "Listar comentários de todas as matérias para moderação")
    public ResponseEntity<GetNewsCommentsResponseDto> adminFindComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) NewsCommentStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String articleId
    ) {
        return ResponseEntity.ok(newsAdminService.adminFindComments(page, limit, status, search, articleId));
    }

    @PatchMapping("/comments/{commentId}/status")
    @Operation(summary = "Alterar status de moderação do comentário (ACTIVE ou HIDDEN)")
    public ResponseEntity<NewsCommentDto> adminUpdateCommentStatus(
            @PathVariable UUID commentId,
            @RequestBody Map<String, String> body
    ) {
        NewsCommentStatus status = NewsCommentStatus.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(newsAdminService.adminUpdateCommentStatus(commentId, status));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Excluir comentário na moderação (soft-delete)")
    public ResponseEntity<Map<String, Object>> adminDeleteComment(@PathVariable UUID commentId) {
        newsAdminService.adminDeleteComment(commentId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Comentário excluído com sucesso."));
    }

    @GetMapping("/showcase")
    @Operation(summary = "Obter dados atuais da vitrine da capa (Manchetão e Destaques Secundários)")
    public ResponseEntity<ShowcaseResponseDto> getShowcase() {
        return ResponseEntity.ok(newsAdminService.getShowcase());
    }

    @PutMapping("/showcase")
    @Operation(summary = "Atualizar os slots da vitrine da capa de forma atômica (Manchetão e Secundários)")
    public ResponseEntity<Map<String, Object>> updateShowcase(@RequestBody UpdateShowcaseDto dto) {
        newsAdminService.updateShowcase(dto);
        return ResponseEntity.ok(Map.of("success", true, "message", "Vitrine da capa atualizada com sucesso."));
    }

    @GetMapping("/current-main")
    @Operation(summary = "Verificar se já existe um Manchetão Principal ativo na capa")
    public ResponseEntity<NewsArticleDto> getCurrentMain() {
        return ResponseEntity.ok(newsAdminService.getCurrentMain());
    }

    @GetMapping("/scheduled")
    @Operation(summary = "Listar matérias agendadas que ainda não entraram no ar (ordem cronológica)")
    public ResponseEntity<Map<String, Object>> getScheduledArticles() {
        return ResponseEntity.ok(newsAdminService.getScheduledArticles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter dados de uma matéria para edição por ID")
    public ResponseEntity<NewsArticleDto> adminFindById(@PathVariable UUID id) {
        return ResponseEntity.ok(newsAdminService.adminFindById(id));
    }

    @PostMapping
    @Operation(summary = "Criar uma nova matéria (rascunho ou publicada)")
    public ResponseEntity<NewsArticleDto> create(
            @RequestBody @Valid CreateNewsDto dto,
            @CurrentUser UserPrincipal principal
    ) {
        String authorId = principal != null ? principal.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(newsAdminService.create(dto, authorId));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar uma matéria existente")
    public ResponseEntity<NewsArticleDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateNewsDto dto,
            @CurrentUser UserPrincipal principal
    ) {
        String userId = principal != null ? principal.getId() : null;
        return ResponseEntity.ok(newsAdminService.update(id, dto, userId));
    }

    @PostMapping("/{id}/duplicate")
    @Operation(summary = "Duplicar uma matéria existente criando um novo rascunho")
    public ResponseEntity<NewsArticleDto> duplicate(
            @PathVariable UUID id,
            @CurrentUser UserPrincipal principal
    ) {
        String userId = principal != null ? principal.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(newsAdminService.duplicate(id, userId));
    }

    @PostMapping("/{id}/publish-now")
    @Operation(summary = "Publicar imediatamente uma matéria agendada no portal")
    public ResponseEntity<Map<String, Object>> publishNow(@PathVariable UUID id) {
        NewsArticleDto published = newsAdminService.publishNow(id);
        return ResponseEntity.ok(Map.of("data", published, "message", "Matéria publicada com sucesso no portal!"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir uma matéria (soft-delete)")
    public ResponseEntity<Map<String, Object>> softDelete(@PathVariable UUID id) {
        newsAdminService.softDelete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Matéria excluída com sucesso (soft-delete)."));
    }

    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload da imagem de capa da matéria")
    public ResponseEntity<Map<String, String>> uploadCover(
            @PathVariable UUID id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "cover", required = false) MultipartFile cover
    ) {
        MultipartFile target = file != null ? file : cover;
        if (target == null || target.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de capa não informado.");
        }
        return ResponseEntity.ok(newsAdminService.uploadCover(id, target));
    }

    @PostMapping(value = "/{id}/gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de múltiplas fotos para a galeria da matéria")
    public ResponseEntity<Map<String, List<String>>> uploadGallery(
            @PathVariable UUID id,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "gallery", required = false) List<MultipartFile> gallery,
            @RequestParam(value = "file", required = false) List<MultipartFile> singleFiles
    ) {
        List<MultipartFile> target = files != null && !files.isEmpty() ? files
                : (gallery != null && !gallery.isEmpty() ? gallery : singleFiles);
        if (target == null || target.isEmpty()) {
            throw new IllegalArgumentException("Nenhum arquivo enviado para a galeria.");
        }
        return ResponseEntity.ok(newsAdminService.uploadGallery(id, target));
    }
}
