package com.esporteclubepelotas.api.modules.football.controller;

import com.esporteclubepelotas.api.modules.football.dto.AthleteDto;
import com.esporteclubepelotas.api.modules.football.dto.GetAthletesResponseDto;
import com.esporteclubepelotas.api.modules.football.dto.QueryAthletesDto;
import com.esporteclubepelotas.api.modules.football.service.AthletesPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/athletes")
@RequiredArgsConstructor
@Tag(name = "Futebol: Atletas (Portal Público)", description = "Consulta pública de atletas e fichas técnicas")
public class AthletesPublicController {

    private final AthletesPublicService athletesPublicService;

    @GetMapping
    @Operation(summary = "Listar atletas cadastrados com filtros de busca e posição")
    public ResponseEntity<GetAthletesResponseDto> findAll(@ModelAttribute QueryAthletesDto query) {
        return ResponseEntity.ok(athletesPublicService.findAll(query));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Obter perfil e ficha técnica de um atleta por slug com histórico de temporadas")
    public ResponseEntity<AthleteDto> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(athletesPublicService.findBySlug(slug));
    }
}

