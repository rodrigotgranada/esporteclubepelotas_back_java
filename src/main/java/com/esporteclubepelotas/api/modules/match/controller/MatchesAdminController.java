package com.esporteclubepelotas.api.modules.match.controller;

import com.esporteclubepelotas.api.modules.match.dto.MatchRequest;
import com.esporteclubepelotas.api.modules.match.dto.MatchResponse;
import com.esporteclubepelotas.api.modules.match.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/matches/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
@Tag(name = "Matches (Admin)", description = "Gestão de partidas do Pelotas")
public class MatchesAdminController {

    private final MatchService matchService;

    @GetMapping
    @Operation(summary = "Listar todas as partidas cadastradas")
    public ResponseEntity<List<MatchResponse>> getAll() {
        return ResponseEntity.ok(matchService.findAllActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter partida por ID")
    public ResponseEntity<MatchResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(matchService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova partida")
    public ResponseEntity<MatchResponse> create(@Valid @RequestBody MatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados de uma partida")
    public ResponseEntity<MatchResponse> update(@PathVariable UUID id, @Valid @RequestBody MatchRequest request) {
        return ResponseEntity.ok(matchService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir partida (soft-delete)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        matchService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de escudo de adversário ou logo")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "image", required = false) MultipartFile imageLegacy
    ) {
        MultipartFile target = file != null ? file : imageLegacy;
        String url = matchService.uploadImage(target);
        return ResponseEntity.ok(Map.of("url", url));
    }
}

