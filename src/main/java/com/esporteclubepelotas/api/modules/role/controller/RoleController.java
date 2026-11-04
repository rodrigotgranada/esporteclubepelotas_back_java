package com.esporteclubepelotas.api.modules.role.controller;

import com.esporteclubepelotas.api.modules.role.entity.Role;
import com.esporteclubepelotas.api.modules.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleRepository roleRepository;

    @GetMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }
}

