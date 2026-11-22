package com.esporteclubepelotas.api.modules.role.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.modules.role.dto.CreateRoleDto;
import com.esporteclubepelotas.api.modules.role.dto.UpdateRoleDto;
import com.esporteclubepelotas.api.modules.role.entity.Role;
import com.esporteclubepelotas.api.modules.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> findAll() {
        List<Role> roles = roleRepository.findAll();
        roles.forEach(this::detachPermissions);
        return roles;
    }

    public Role findById(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", "id", id));
        detachPermissions(role);
        return role;
    }

    @Transactional
    public Role createRole(CreateRoleDto dto) {
        String normalizedName = dto.getName().trim().toUpperCase();
        if (roleRepository.findByName(normalizedName).isPresent()) {
            throw new BusinessException("Já existe um cargo com o identificador: " + normalizedName);
        }

        String label = (dto.getLabel() != null && !dto.getLabel().isBlank())
                ? dto.getLabel().trim()
                : normalizedName;

        Role role = Role.builder()
                .name(normalizedName)
                .label(label)
                .description(dto.getDescription() != null ? dto.getDescription().trim() : "")
                .permissions(dto.getPermissions() != null ? new ArrayList<>(dto.getPermissions()) : new ArrayList<>())
                .build();

        log.info("Criando novo cargo: {}", normalizedName);
        Role saved = roleRepository.save(role);
        detachPermissions(saved);
        return saved;
    }

    @Transactional
    public Role updateRole(String id, UpdateRoleDto dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", "id", id));

        if (role.isImmutable()) {
            throw new BusinessException("Cargos nativos e imutáveis não podem ter suas permissões principais alteradas.");
        }

        if (dto.getLabel() != null) {
            role.setLabel(dto.getLabel().trim());
        }
        if (dto.getDescription() != null) {
            role.setDescription(dto.getDescription().trim());
        }
        if (dto.getPermissions() != null) {
            role.setPermissions(new ArrayList<>(dto.getPermissions()));
        }

        log.info("Atualizando cargo: {}", role.getName());
        Role saved = roleRepository.save(role);
        detachPermissions(saved);
        return saved;
    }

    @Transactional
    public void deleteRole(String id) {
        Role role = findById(id);

        if (role.isImmutable()) {
            throw new BusinessException("Cargos do sistema são imutáveis e não podem ser excluídos.");
        }

        log.info("Excluindo cargo: {}", role.getName());
        roleRepository.delete(role);
    }

    private void detachPermissions(Role role) {
        if (role != null && role.getPermissions() != null) {
            role.setPermissions(new ArrayList<>(role.getPermissions()));
        }
    }
}
