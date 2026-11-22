package com.esporteclubepelotas.api.modules.match.controller;

import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.modules.match.dto.ChampionshipResponse;
import com.esporteclubepelotas.api.modules.match.dto.MatchResponse;
import com.esporteclubepelotas.api.modules.match.service.ChampionshipService;
import com.esporteclubepelotas.api.modules.match.service.MatchService;
import com.esporteclubepelotas.api.modules.systemmodule.service.SystemModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/matches/public")
@RequiredArgsConstructor
@Tag(name = "Matches (Público)", description = "Calendário de jogos, próximo confronto e campeonatos")
public class MatchesPublicController {

    private final MatchService matchService;
    private final ChampionshipService championshipService;
    private final SystemModuleService systemModuleService;

    @GetMapping
    @Operation(summary = "Listar todas as partidas ativas ordenadas por data")
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        if (!systemModuleService.isModuleActive("matches")) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(matchService.findAllActive());
    }

    @GetMapping("/next")
    @Operation(summary = "Obter o próximo jogo do Pelotas (ou em andamento)")
    public ResponseEntity<MatchResponse> getNextMatch() {
        if (!systemModuleService.isModuleActive("matches")) {
            return ResponseEntity.noContent().build();
        }
        return matchService.findNextMatch()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/championship/{championshipId}")
    @Operation(summary = "Listar partidas de um campeonato específico")
    public ResponseEntity<List<MatchResponse>> getByChampionship(@PathVariable UUID championshipId) {
        if (!systemModuleService.isModuleActive("matches")) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(matchService.findByChampionship(championshipId));
    }

    @GetMapping("/championships")
    @Operation(summary = "Listar campeonatos ativos da temporada")
    public ResponseEntity<List<ChampionshipResponse>> getActiveChampionships() {
        if (!systemModuleService.isModuleActive("matches")) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(championshipService.findCurrentActive());
    }

    @GetMapping("/by-slug/{slug}")
    @Operation(summary = "Obter detalhes de uma partida por slug amigável")
    public ResponseEntity<MatchResponse> getBySlug(@PathVariable String slug) {
        if (!systemModuleService.isModuleActive("matches")) {
            throw new ResourceNotFoundException("Partida", "slug", slug);
        }
        return ResponseEntity.ok(matchService.findBySlug(slug));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes de uma partida por ID")
    public ResponseEntity<MatchResponse> getById(@PathVariable UUID id) {
        if (!systemModuleService.isModuleActive("matches")) {
            throw new ResourceNotFoundException("Partida", "id", id.toString());
        }
        return ResponseEntity.ok(matchService.findById(id));
    }
}

