package com.esporteclubepelotas.api.modules.systemmodule.service;

import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;
import com.esporteclubepelotas.api.modules.systemmodule.dto.UpdateSystemModuleStatusDto;
import com.esporteclubepelotas.api.modules.systemmodule.entity.SystemModule;
import com.esporteclubepelotas.api.modules.systemmodule.repository.SystemModuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SystemModuleService {

    private final SystemModuleRepository moduleRepository;

    @Cacheable(value = "systemModules")
    public List<SystemModule> findAll() {
        return moduleRepository.findAllByOrderByCreatedAtAsc();
    }

    public SystemModule findById(String id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Módulo", "id", id));
    }

    @CacheEvict(value = "systemModules", allEntries = true)
    @AuditLog(module = "SYSTEM_MODULES", action = "TOGGLE", description = "Alternância de Feature Flag do Módulo")
    @Transactional
    public SystemModule toggleStatus(String id, UpdateSystemModuleStatusDto dto) {
        SystemModule module = findById(id);

        if (dto != null && dto.getIsActive() != null) {
            module.setActive(dto.getIsActive());
        } else {
            module.setActive(!module.isActive());
        }

        log.info("Status do módulo {} alterado para: {}", module.getName(), module.isActive());
        return moduleRepository.save(module);
    }
}
