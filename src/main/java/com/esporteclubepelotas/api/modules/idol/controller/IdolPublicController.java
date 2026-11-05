package com.esporteclubepelotas.api.modules.idol.controller;

import com.esporteclubepelotas.api.modules.idol.dto.GetIdolsResponseDto;
import com.esporteclubepelotas.api.modules.idol.dto.IdolDto;
import com.esporteclubepelotas.api.modules.idol.dto.PrevNextIdolResponseDto;
import com.esporteclubepelotas.api.modules.idol.entity.IdolCategory;
import com.esporteclubepelotas.api.modules.idol.service.IdolPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/idols")
@RequiredArgsConstructor
@Tag(name = "Ídolos (Portal Público)", description = "Galeria e Memorial Histórico de Grandes Figuras do Pelotas")
public class IdolPublicController {

    private final IdolPublicService idolPublicService;

    @GetMapping
    @Operation(summary = "Listar ídolos do Memorial com paginação e filtros")
    public ResponseEntity<GetIdolsResponseDto> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) IdolCategory category,
            @RequestParam(required = false) Boolean isFeatured) {
        return ResponseEntity.ok(idolPublicService.findAll(page, limit, search, category, isFeatured));
    }

    @GetMapping("/featured")
    @Operation(summary = "Listar ídolos em destaque para a home e vitrines")
    public ResponseEntity<List<IdolDto>> findFeatured(@RequestParam(defaultValue = "4") int limit) {
        return ResponseEntity.ok(idolPublicService.findFeatured(limit));
    }

    @GetMapping("/{slug}/prev-next")
    @Operation(summary = "Obter ídolo anterior e próximo na linha cronológica do Memorial")
    public ResponseEntity<PrevNextIdolResponseDto> getPrevNext(@PathVariable String slug) {
        return ResponseEntity.ok(idolPublicService.getPrevNext(slug));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Obter perfil detalhado de um ídolo por slug")
    public ResponseEntity<IdolDto> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(idolPublicService.findBySlug(slug));
    }
}

