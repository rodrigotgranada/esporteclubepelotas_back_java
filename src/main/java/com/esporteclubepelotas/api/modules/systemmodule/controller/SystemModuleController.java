package com.esporteclubepelotas.api.modules.systemmodule.controller;

import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;

import com.esporteclubepelotas.api.modules.systemmodule.dto.UpdateSystemModuleStatusDto;
import com.esporteclubepelotas.api.modules.systemmodule.entity.SystemModule;
import com.esporteclubepelotas.api.modules.systemmodule.service.SystemModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({ "/admin/modules", "/system-modules" })
@RequiredArgsConstructor
@Tag(name = "System Modules", description = "Controle de Feature Flags e Módulos do Sistema")

public class SystemModuleController {

    private final SystemModuleService systemModuleService;

    @GetMapping
    @Operation(summary = "Listar todos os módulos e status de feature flag")
    public ResponseEntity<List<SystemModule>> getModules() {
        return ResponseEntity.ok(systemModuleService.findAll());
    }

    @PatchMapping({ "/{id}/status", "/{id}/toggle" })
    @AuditLog(module = "SYSTEM_MODULES", action = "TOGGLE_MODULE", description = "Alteração de status de módulo do sistema")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Ativar ou desativar módulo (Feature Flag)")
    public ResponseEntity<SystemModule> updateModuleStatus(
            @PathVariable String id,
            @RequestBody(required = false) UpdateSystemModuleStatusDto dto) {
        return ResponseEntity.ok(systemModuleService.toggleStatus(id, dto));
    }
}
