package com.esporteclubepelotas.api.modules.symbol.controller;

import com.esporteclubepelotas.api.modules.symbol.dto.ClubSymbolOverviewResponse;
import com.esporteclubepelotas.api.modules.symbol.dto.ClubSymbolResponse;
import com.esporteclubepelotas.api.modules.symbol.entity.SymbolCategory;
import com.esporteclubepelotas.api.modules.symbol.service.ClubSymbolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/symbols")
@RequiredArgsConstructor
@Tag(name = "Público - Símbolos & Mascote", description = "Consulta pública de mascotes, escudos históricos, bandeira e cores")
public class ClubSymbolPublicController {

    private final ClubSymbolService symbolService;

    @GetMapping("/overview")
    @Operation(summary = "Listar visões gerais e narrativas institucionais de símbolos")
    public ResponseEntity<List<ClubSymbolOverviewResponse>> listOverviews() {
        return ResponseEntity.ok(symbolService.listAllOverviews());
    }

    @GetMapping("/overview/{category}")
    @Operation(summary = "Obter visão geral de uma categoria (MASCOT, SHIELD, FLAG_COLORS)")
    public ResponseEntity<ClubSymbolOverviewResponse> getOverviewByCategory(@PathVariable String category) {
        return ResponseEntity.ok(symbolService.getOverviewByCategory(category));
    }

    @GetMapping
    @Operation(summary = "Listar todos os símbolos ativos do clube")
    public ResponseEntity<List<ClubSymbolResponse>> listAll() {
        return ResponseEntity.ok(symbolService.listAllForPublic());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Listar símbolos por categoria (MASCOT, SHIELD, FLAG, COLORS)")
    public ResponseEntity<List<ClubSymbolResponse>> listByCategory(@PathVariable SymbolCategory category) {
        return ResponseEntity.ok(symbolService.listPublicByCategory(category));
    }

    @GetMapping("/category/{category}/official")
    @Operation(summary = "Obter o símbolo oficial principal de uma categoria")
    public ResponseEntity<ClubSymbolResponse> getOfficialByCategory(@PathVariable SymbolCategory category) {
        return ResponseEntity.ok(symbolService.getOfficialByCategory(category));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes de um símbolo por ID")
    public ResponseEntity<ClubSymbolResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(symbolService.getById(id));
    }
}

