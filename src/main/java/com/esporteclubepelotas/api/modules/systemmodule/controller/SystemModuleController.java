package com.esporteclubepelotas.api.modules.systemmodule.controller;

import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.modules.systemmodule.entity.SystemModule;
import com.esporteclubepelotas.api.modules.systemmodule.repository.SystemModuleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({ "/admin/modules", "/system-modules" })
@RequiredArgsConstructor
@Tag(name = "System Modules", description = "Controle de Feature Flags e Módulos do Sistema")
public class SystemModuleController {

    private final SystemModuleRepository moduleRepository;

    @GetMapping
    @Operation(summary = "Listar todos os módulos e status de feature flag")
    public ResponseEntity<List<SystemModule>> getModules() {
        return ResponseEntity.ok(moduleRepository.findAllByOrderByCreatedAtAsc());
    }

    @PatchMapping({ "/{id}/status", "/{id}/toggle" })
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Ativar ou desativar módulo (Feature Flag)")
    public ResponseEntity<SystemModule> updateModuleStatus(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, Object> payload) {
        SystemModule module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Módulo", "id", id));

        if (payload != null && payload.containsKey("isActive")) {
            module.setActive(Boolean.TRUE.equals(payload.get("isActive")));
        } else {
            module.setActive(!module.isActive());
        }

        return ResponseEntity.ok(moduleRepository.save(module));
    }
}
