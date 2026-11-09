package com.esporteclubepelotas.api.modules.symbol.controller;

import com.esporteclubepelotas.api.modules.symbol.dto.ClubSymbolRequest;
import com.esporteclubepelotas.api.modules.symbol.dto.ClubSymbolResponse;
import com.esporteclubepelotas.api.modules.symbol.entity.SymbolCategory;
import com.esporteclubepelotas.api.modules.symbol.service.ClubSymbolService;
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
@RequestMapping("/admin/symbols")
@RequiredArgsConstructor
@Tag(name = "Admin - Símbolos & Mascote", description = "Gestão de mascotes, escudos históricos, bandeira e cores")
@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
public class ClubSymbolAdminController {

    private final ClubSymbolService symbolService;

    @GetMapping
    @Operation(summary = "Listar todos os símbolos para administração")
    public ResponseEntity<List<ClubSymbolResponse>> listAll() {
        return ResponseEntity.ok(symbolService.listAllForAdmin());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Listar símbolos por categoria para administração")
    public ResponseEntity<List<ClubSymbolResponse>> listByCategory(@PathVariable SymbolCategory category) {
        return ResponseEntity.ok(symbolService.listAdminByCategory(category));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter símbolo por ID")
    public ResponseEntity<ClubSymbolResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(symbolService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo símbolo ou versão")
    public ResponseEntity<ClubSymbolResponse> create(@Valid @RequestBody ClubSymbolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(symbolService.createSymbol(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar símbolo existente")
    public ResponseEntity<ClubSymbolResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ClubSymbolRequest request
    ) {
        return ResponseEntity.ok(symbolService.updateSymbol(id, request));
    }

    @PatchMapping("/{id}/official")
    @Operation(summary = "Definir símbolo como a versão oficial da categoria")
    public ResponseEntity<ClubSymbolResponse> setOfficial(@PathVariable UUID id) {
        return ResponseEntity.ok(symbolService.setOfficial(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir símbolo (Soft delete)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        symbolService.deleteSymbol(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de imagem principal ou item de galeria do símbolo")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        MultipartFile target = file != null ? file : image;
        return ResponseEntity.ok(symbolService.uploadImage(target));
    }
}
