package com.esporteclubepelotas.api.modules.role.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;
import com.esporteclubepelotas.api.modules.role.dto.CreateRoleDto;
import com.esporteclubepelotas.api.modules.role.dto.UpdateRoleDto;
import com.esporteclubepelotas.api.modules.role.entity.Role;
import com.esporteclubepelotas.api.modules.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findById(String id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", "id", id));
    }

    @AuditLog(module = "ROLES", action = "CREATE", description = "Criação de novo cargo no sistema")
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
                .permissions(dto.getPermissions() != null ? dto.getPermissions() : List.of())
                .build();

        log.info("Criando novo cargo: {}", normalizedName);
        return roleRepository.save(role);
    }

    @AuditLog(module = "ROLES", action = "UPDATE", description = "Atualização de permissões ou dados do cargo")
    @Transactional
    public Role updateRole(String id, UpdateRoleDto dto) {
        Role role = findById(id);

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
            role.setPermissions(dto.getPermissions());
        }

        log.info("Atualizando cargo: {}", role.getName());
        return roleRepository.save(role);
    }

    @AuditLog(module = "ROLES", action = "DELETE", description = "Exclusão de cargo no sistema")
    @Transactional
    public void deleteRole(String id) {
        Role role = findById(id);

        if (role.isImmutable()) {
            throw new BusinessException("Cargos do sistema são imutáveis e não podem ser excluídos.");
        }

        log.info("Excluindo cargo: {}", role.getName());
        roleRepository.delete(role);
    }
}

