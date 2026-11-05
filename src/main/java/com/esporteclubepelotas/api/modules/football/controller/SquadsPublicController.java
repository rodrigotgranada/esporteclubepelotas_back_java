package com.esporteclubepelotas.api.modules.football.controller;

import com.esporteclubepelotas.api.modules.football.dto.GetSquadsResponseDto;
import com.esporteclubepelotas.api.modules.football.dto.QuerySquadsDto;
import com.esporteclubepelotas.api.modules.football.dto.SquadDto;
import com.esporteclubepelotas.api.modules.football.dto.SquadSeasonsResponseDto;
import com.esporteclubepelotas.api.modules.football.entity.SquadCategory;
import com.esporteclubepelotas.api.modules.football.service.SquadsPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/squads")
@RequiredArgsConstructor
@Tag(name = "Futebol: Elencos & Temporadas (Portal Público)", description = "Consulta pública de elencos, temporadas e setores táticos")
public class SquadsPublicController {

    private final SquadsPublicService squadsPublicService;

    @GetMapping("/seasons")
    @Operation(summary = "Listar temporadas e categorias disponíveis para filtros no portal")
    public ResponseEntity<SquadSeasonsResponseDto> getSeasons() {
        return ResponseEntity.ok(squadsPublicService.getSeasons());
    }

    @GetMapping("/current")
    @Operation(summary = "Obter elenco atual em atividade organizado por setores táticos")
    public ResponseEntity<SquadDto> getCurrent(@RequestParam(required = false) SquadCategory category) {
        return ResponseEntity.ok(squadsPublicService.getCurrent(category));
    }

    @GetMapping("/{year}/{category}")
    @Operation(summary = "Obter elenco de um ano e categoria específicos organizado por setores")
    public ResponseEntity<SquadDto> getByYearAndCategory(
            @PathVariable int year,
            @PathVariable SquadCategory category
    ) {
        return ResponseEntity.ok(squadsPublicService.getByYearAndCategory(year, category));
    }

    @GetMapping
    @Operation(summary = "Listar elencos com filtros de ano e categoria")
    public ResponseEntity<GetSquadsResponseDto> findAll(@ModelAttribute QuerySquadsDto query) {
        return ResponseEntity.ok(squadsPublicService.findAll(query));
    }
}

