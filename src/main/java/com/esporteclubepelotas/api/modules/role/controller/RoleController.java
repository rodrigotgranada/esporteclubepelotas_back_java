package com.esporteclubepelotas.api.modules.role.controller;

import com.esporteclubepelotas.api.modules.role.dto.CreateRoleDto;
import com.esporteclubepelotas.api.modules.role.dto.UpdateRoleDto;
import com.esporteclubepelotas.api.modules.role.entity.Role;
import com.esporteclubepelotas.api.modules.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping({"/admin/roles", "/roles"})
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Gestão de Cargos e Permissões de Acesso (RBAC)")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR')")
    @Operation(summary = "Listar todos os cargos cadastrados")
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Obter dados de um cargo por ID")
    public ResponseEntity<Role> getRoleById(@PathVariable String id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Criar um novo cargo")
    public ResponseEntity<Role> createRole(@Valid @RequestBody CreateRoleDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Atualizar permissões ou dados de um cargo")
    public ResponseEntity<Role> updateRole(
            @PathVariable String id,
            @Valid @RequestBody UpdateRoleDto dto
    ) {
        return ResponseEntity.ok(roleService.updateRole(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Excluir um cargo")
    public ResponseEntity<Map<String, Boolean>> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
