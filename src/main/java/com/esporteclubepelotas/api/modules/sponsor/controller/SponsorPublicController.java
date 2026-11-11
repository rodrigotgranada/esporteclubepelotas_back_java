package com.esporteclubepelotas.api.modules.sponsor.controller;

import com.esporteclubepelotas.api.modules.sponsor.dto.SponsorDto;
import com.esporteclubepelotas.api.modules.sponsor.service.SponsorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/sponsors")
@RequiredArgsConstructor
@Tag(name = "Patrocinadores (Público)", description = "Visualização de patrocinadores ativos e parceiros da home")
public class SponsorPublicController {

    private final SponsorService sponsorService;

    @GetMapping
    @Operation(summary = "Listar patrocinadores oficiais ativos em vigência para o rodapé/seção da home ou hubs segmentados")
    public ResponseEntity<List<SponsorDto>> getActiveSponsors(
            @RequestParam(required = false) String scope
    ) {
        return ResponseEntity.ok(sponsorService.findActivePublicSponsors(scope));
    }

    @GetMapping("/featured")
    @Operation(summary = "Listar patrocinadores destacados para o ticker/marquee superior da home ou hubs segmentados")
    public ResponseEntity<List<SponsorDto>> getFeaturedSponsors(
            @RequestParam(required = false) String scope
    ) {
        return ResponseEntity.ok(sponsorService.findFeaturedPublicSponsors(scope));
    }

    @PostMapping("/{id}/click")
    @Operation(summary = "Registrar clique no patrocinador para prestação de contas e métricas")
    public ResponseEntity<Map<String, Object>> trackClick(@PathVariable UUID id) {
        sponsorService.incrementClick(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}

