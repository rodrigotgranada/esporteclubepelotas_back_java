package com.esporteclubepelotas.api.modules.news.controller;

import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.news.dto.*;
import com.esporteclubepelotas.api.modules.news.service.NewsPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Tag(name = "News (Público)", description = "Endpoints públicos de leitura, reações e comentários de matérias")
public class NewsPublicController {

    private final NewsPublicService newsPublicService;

    @GetMapping
    @Operation(summary = "Listar matérias públicas com paginação e filtros")
    public ResponseEntity<GetNewsResponseDto> findAll(@ModelAttribute QueryNewsDto query) {
        return ResponseEntity.ok(newsPublicService.findAll(query));
    }

    @GetMapping("/featured")
    @Operation(summary = "Obter matérias em destaque para carrossel ou capa")
    public ResponseEntity<List<NewsArticleDto>> findFeatured(@RequestParam(defaultValue = "4") int limit) {
        return ResponseEntity.ok(newsPublicService.findFeatured(limit));
    }

    @GetMapping("/categories")
    @Operation(summary = "Listar categorias públicas ativas")
    public ResponseEntity<List<NewsCategoryDto>> findAllCategories() {
        return ResponseEntity.ok(newsPublicService.findAllCategories());
    }

    @GetMapping("/{slug}/prev-next")
    @Operation(summary = "Obter matéria anterior e próxima cronológica")
    public ResponseEntity<PrevNextNewsResponseDto> getPrevNext(@PathVariable String slug) {
        return ResponseEntity.ok(newsPublicService.getPrevNext(slug));
    }

    @GetMapping("/{slug}/my-reaction")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obter a reação do usuário logado na matéria")
    public ResponseEntity<Map<String, Object>> getMyReaction(
            @PathVariable String slug,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        String userId = principal != null ? principal.getId() : null;
        return ResponseEntity.ok(newsPublicService.getUserReaction(slug, userId));
    }

    @PostMapping("/{slug}/reactions")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reagir a uma matéria com reação áureo-cerúlea (apenas usuários logados e ativos)")
    public ResponseEntity<ReactNewsResponseDto> reactToArticle(
            @PathVariable String slug,
            @RequestBody @Valid ReactNewsDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        String userId = principal != null ? principal.getId() : null;
        return ResponseEntity.ok(newsPublicService.reactToArticle(slug, userId, dto.getType()));
    }

    @GetMapping("/{slug}/comments")
    @Operation(summary = "Listar comentários públicos de uma matéria com paginação")
    public ResponseEntity<GetNewsCommentsResponseDto> getComments(
            @PathVariable String slug,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        return ResponseEntity.ok(newsPublicService.getComments(slug, page, limit));
    }

    @PostMapping("/{slug}/comments")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Adicionar comentário em uma matéria (apenas usuários autenticados e ativos)")
    public ResponseEntity<NewsCommentDto> addComment(
            @PathVariable String slug,
            @RequestBody @Valid CreateNewsCommentDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        String userId = principal != null ? principal.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(newsPublicService.addComment(slug, userId, dto.getContent()));
    }

    @PostMapping("/comments/{commentId}/report")
    @Operation(summary = "Denunciar um comentário por violação de diretrizes")
    public ResponseEntity<NewsCommentDto> reportComment(
            @PathVariable UUID commentId,
            @RequestBody(required = false) Map<String, String> body
    ) {
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(newsPublicService.reportComment(commentId, reason));
    }

    @DeleteMapping("/comments/{commentId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remover um comentário (próprio autor ou moderador)")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @PathVariable UUID commentId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        String userId = principal != null ? principal.getId() : null;
        String userRole = principal != null ? principal.getRole() : "USER";
        newsPublicService.deleteComment(commentId, userId, userRole);
        return ResponseEntity.ok(Map.of("success", true, "message", "Comentário removido com sucesso."));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Obter detalhes de uma matéria pública por slug")
    public ResponseEntity<NewsArticleDto> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(newsPublicService.findBySlug(slug));
    }
}
