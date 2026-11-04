package com.esporteclubepelotas.api.modules.role.controller;

import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.modules.role.entity.Role;
import com.esporteclubepelotas.api.modules.role.repository.RoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    private final RoleRepository roleRepository;

    @GetMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR')")
    @Operation(summary = "Listar todos os cargos cadastrados")
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Obter dados de um cargo por ID")
    public ResponseEntity<Role> getRoleById(@PathVariable String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", "id", id));
        return ResponseEntity.ok(role);
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Criar um novo cargo")
    public ResponseEntity<Role> createRole(@RequestBody Map<String, Object> payload) {
        String name = (String) payload.get("name");
        String label = (String) payload.getOrDefault("label", name);
        String description = (String) payload.getOrDefault("description", "");
        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) payload.getOrDefault("permissions", List.of());

        Role role = Role.builder()
                .name(name.toUpperCase())
                .label(label)
                .description(description)
                .permissions(permissions)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(roleRepository.save(role));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Atualizar permissões ou dados de um cargo")
    public ResponseEntity<Role> updateRole(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", "id", id));

        if (payload.containsKey("label")) role.setLabel((String) payload.get("label"));
        if (payload.containsKey("description")) role.setDescription((String) payload.get("description"));
        if (payload.containsKey("permissions")) {
            @SuppressWarnings("unchecked")
            List<String> perms = (List<String>) payload.get("permissions");
            role.setPermissions(perms);
        }

        return ResponseEntity.ok(roleRepository.save(role));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Excluir um cargo")
    public ResponseEntity<Map<String, Boolean>> deleteRole(@PathVariable String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", "id", id));
        roleRepository.delete(role);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
