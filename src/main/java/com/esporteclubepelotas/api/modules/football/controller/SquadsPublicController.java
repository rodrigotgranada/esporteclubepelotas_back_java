package com.esporteclubepelotas.api.modules.football.controller;

import com.esporteclubepelotas.api.modules.football.dto.GetSquadsResponseDto;
import com.esporteclubepelotas.api.modules.football.dto.QuerySquadsDto;
import com.esporteclubepelotas.api.modules.football.dto.SquadDto;
import com.esporteclubepelotas.api.modules.football.dto.SquadSeasonsResponseDto;
import com.esporteclubepelotas.api.modules.football.dto.YouthPortalConfigDto;
import com.esporteclubepelotas.api.modules.football.entity.SquadCategory;
import com.esporteclubepelotas.api.modules.football.service.SquadsPublicService;
import com.esporteclubepelotas.api.modules.football.service.YouthPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/squads")
@RequiredArgsConstructor
@Tag(name = "Futebol: Elencos & Temporadas (Portal Público)", description = "Consulta pública de elencos, temporadas, setores táticos e portal da base")
public class SquadsPublicController {

    private final SquadsPublicService squadsPublicService;
    private final YouthPortalService youthPortalService;

    @GetMapping("/seasons")
    @Operation(summary = "Listar temporadas e categorias disponíveis para filtros no portal")
    public ResponseEntity<SquadSeasonsResponseDto> getSeasons() {
        return ResponseEntity.ok(squadsPublicService.getSeasons());
    }

    @GetMapping("/youth-config")
    @Operation(summary = "Obter configurações dinâmicas do Portal da Base (Pilares, Critérios de Peneiras e Contatos)")
    public ResponseEntity<YouthPortalConfigDto> getYouthConfig() {
        return ResponseEntity.ok(youthPortalService.getConfig());
    }

    @GetMapping("/current")
    @Operation(summary = "Obter elenco atual em atividade organizado por setores táticos")
    public ResponseEntity<SquadDto> getCurrent(@RequestParam(required = false) SquadCategory category) {
        return ResponseEntity.ok(squadsPublicService.getCurrent(category));
    }

    @GetMapping({"/id/{id}", "/detail/{id}"})
    @Operation(summary = "Obter elenco específico por ID organizado por setores táticos")
    public ResponseEntity<SquadDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(squadsPublicService.getById(id));
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
