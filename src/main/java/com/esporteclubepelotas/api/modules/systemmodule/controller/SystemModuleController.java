package com.esporteclubepelotas.api.modules.systemmodule.controller;

import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.modules.systemmodule.entity.SystemModule;
import com.esporteclubepelotas.api.modules.systemmodule.repository.SystemModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system-modules")
@RequiredArgsConstructor
public class SystemModuleController {

    private final SystemModuleRepository moduleRepository;

    @GetMapping
    public ResponseEntity<List<SystemModule>> getModules() {
        return ResponseEntity.ok(moduleRepository.findAllByOrderByCreatedAtAsc());
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<SystemModule> toggleModule(@PathVariable String id) {
        SystemModule module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Módulo", "id", id));

        module.setActive(!module.isActive());
        return ResponseEntity.ok(moduleRepository.save(module));
    }
}
