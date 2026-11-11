package com.esporteclubepelotas.api.modules.theme.controller;

import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;

import com.esporteclubepelotas.api.modules.theme.dto.ThemeRequestDto;
import com.esporteclubepelotas.api.modules.theme.dto.ThemeResponseDto;
import com.esporteclubepelotas.api.modules.theme.service.ThemeService;
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
import java.util.Map;

@RestController
@RequestMapping("/themes")
@RequiredArgsConstructor
@Tag(name = "Themes", description = "Gerenciamento e consulta de temas (Light/Dark/Campanhas)")

public class ThemeController {

    private final ThemeService themeService;

    @GetMapping("/active")
    @Operation(summary = "Obtém o tema atualmente ativo (Público)", description = "Retorna a campanha ativa vigente ou o tema padrão ativo")
    public ResponseEntity<ThemeResponseDto> getActiveTheme() {
        return ResponseEntity.ok(themeService.getActiveTheme());
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('settings.view', 'settings.manage', 'ROLE_ADMIN', 'ROLE_OWNER')")
    @Operation(summary = "Lista todos os temas cadastrados (Admin)")
    public ResponseEntity<List<ThemeResponseDto>> findAll() {
        return ResponseEntity.ok(themeService.findAll());
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('settings.view', 'settings.manage', 'ROLE_ADMIN', 'ROLE_OWNER')")
    @Operation(summary = "Busca detalhes de um tema por ID (Admin)")
    public ResponseEntity<ThemeResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(themeService.findById(id));
    }

    @PostMapping
    @AuditLog(module = "THEMES", action = "CREATE_THEME", description = "Criação de campanha/tema")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('settings.manage', 'ROLE_ADMIN', 'ROLE_OWNER')")
    @Operation(summary = "Cadastra um novo tema ou campanha sazonal (Admin)")
    public ResponseEntity<ThemeResponseDto> create(@Valid @RequestBody ThemeRequestDto dto) {
        ThemeResponseDto created = themeService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    @AuditLog(module = "THEMES", action = "UPDATE_THEME", description = "Atualização de tema")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('settings.manage', 'ROLE_ADMIN', 'ROLE_OWNER')")
    @Operation(summary = "Atualiza parcialmente os dados ou cores de um tema (Admin)")
    public ResponseEntity<ThemeResponseDto> update(@PathVariable Long id, @RequestBody ThemeRequestDto dto) {
        return ResponseEntity.ok(themeService.update(id, dto));
    }

    @PostMapping("/{id}/activate")
    @AuditLog(module = "THEMES", action = "ACTIVATE_THEME", description = "Ativação de tema")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('settings.manage', 'ROLE_ADMIN', 'ROLE_OWNER')")
    @Operation(summary = "Ativa um tema (Admin)")
    public ResponseEntity<Map<String, Object>> activate(@PathVariable Long id) {
        themeService.activate(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Tema ativado com sucesso"));
    }

    @PostMapping("/{id}/deactivate")
    @AuditLog(module = "THEMES", action = "DEACTIVATE_THEME", description = "Desativação de tema")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('settings.manage', 'ROLE_ADMIN', 'ROLE_OWNER')")
    @Operation(summary = "Desativa um tema de campanha (Admin)")
    public ResponseEntity<Map<String, Object>> deactivate(@PathVariable Long id) {
        themeService.deactivate(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Tema desativado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @AuditLog(module = "THEMES", action = "DELETE_THEME", description = "Exclusão de tema")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('settings.manage', 'ROLE_ADMIN', 'ROLE_OWNER')")
    @Operation(summary = "Exclui um tema não-padrão (Admin)")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        themeService.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Tema removido com sucesso"));
    }
}

