package com.esporteclubepelotas.api.modules.match.controller;

import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;

import com.esporteclubepelotas.api.modules.match.dto.ChampionshipRequest;
import com.esporteclubepelotas.api.modules.match.dto.ChampionshipResponse;
import com.esporteclubepelotas.api.modules.match.service.ChampionshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/championships/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
@Tag(name = "Championships (Admin)", description = "Gestão de campeonatos oficiais")

public class ChampionshipsAdminController {

    private final ChampionshipService championshipService;

    @GetMapping
    @Operation(summary = "Listar todos os campeonatos cadastrados")
    public ResponseEntity<List<ChampionshipResponse>> getAll() {
        return ResponseEntity.ok(championshipService.findAllActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter campeonato por ID")
    public ResponseEntity<ChampionshipResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(championshipService.findById(id));
    }

    @PostMapping
    @AuditLog(module = "CHAMPIONSHIPS", action = "CREATE_CHAMPIONSHIP", description = "Cadastro de campeonato")
    @Operation(summary = "Cadastrar novo campeonato")
    public ResponseEntity<ChampionshipResponse> create(@Valid @RequestBody ChampionshipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(championshipService.create(request));
    }

    @PutMapping("/{id}")
    @AuditLog(module = "CHAMPIONSHIPS", action = "UPDATE_CHAMPIONSHIP", description = "Atualização de campeonato")
    @Operation(summary = "Atualizar campeonato")
    public ResponseEntity<ChampionshipResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ChampionshipRequest request
    ) {
        return ResponseEntity.ok(championshipService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @AuditLog(module = "CHAMPIONSHIPS", action = "DELETE_CHAMPIONSHIP", description = "Exclusão de campeonato")
    @Operation(summary = "Excluir campeonato (soft-delete)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        championshipService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

