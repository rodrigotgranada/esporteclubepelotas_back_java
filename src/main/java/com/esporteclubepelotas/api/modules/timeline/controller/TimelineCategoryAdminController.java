package com.esporteclubepelotas.api.modules.timeline.controller;

import com.esporteclubepelotas.api.modules.timeline.dto.CreateTimelineCategoryDto;
import com.esporteclubepelotas.api.modules.timeline.dto.TimelineCategoryDto;
import com.esporteclubepelotas.api.modules.timeline.dto.UpdateTimelineCategoryDto;
import com.esporteclubepelotas.api.modules.timeline.service.TimelineCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/timeline/admin/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('timeline.manage') or hasRole('ADMIN') or hasRole('OWNER') or hasRole('EDITOR')")
@Tag(name = "Categorias da Linha do Tempo (Gestão)", description = "CRUD de categorias para os marcos históricos")
public class TimelineCategoryAdminController {

    private final TimelineCategoryService timelineCategoryService;

    @GetMapping
    @Operation(summary = "Listar todas as categorias da linha do tempo para administração")
    public ResponseEntity<List<TimelineCategoryDto>> findAll() {
        return ResponseEntity.ok(timelineCategoryService.findAllAdmin());
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova categoria de marco histórico")
    public ResponseEntity<TimelineCategoryDto> create(@RequestBody @Valid CreateTimelineCategoryDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timelineCategoryService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria existente")
    public ResponseEntity<TimelineCategoryDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateTimelineCategoryDto dto
    ) {
        return ResponseEntity.ok(timelineCategoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir ou inativar categoria")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        timelineCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

