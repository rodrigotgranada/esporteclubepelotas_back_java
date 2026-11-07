package com.esporteclubepelotas.api.modules.audit.repository;

import com.esporteclubepelotas.api.modules.audit.entity.AuditLogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntry, String>, JpaSpecificationExecutor<AuditLogEntry> {
    Page<AuditLogEntry> findAll(Specification<AuditLogEntry> spec, Pageable pageable);
}

