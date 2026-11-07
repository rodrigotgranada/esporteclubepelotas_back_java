package com.esporteclubepelotas.api.modules.audit.service;

import com.esporteclubepelotas.api.common.dto.PageResponseDto;
import com.esporteclubepelotas.api.modules.audit.entity.AuditLogEntry;
import com.esporteclubepelotas.api.modules.audit.repository.AuditLogRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public PageResponseDto<AuditLogEntry> findLogs(
            int page,
            int limit,
            String module,
            String action,
            String actorEmail,
            String search
    ) {
        int pageIndex = Math.max(page - 1, 0);
        int pageSize = limit > 0 ? Math.min(limit, 100) : 20;

        Specification<AuditLogEntry> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (module != null && !module.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("module")), module.trim().toUpperCase()));
            }

            if (action != null && !action.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("action")), action.trim().toUpperCase()));
            }

            if (actorEmail != null && !actorEmail.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("actorEmail")), "%" + actorEmail.trim().toLowerCase() + "%"));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(cb.coalesce(root.get("details"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("actorName"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("entityId"), "")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Page<AuditLogEntry> paged = auditLogRepository.findAll(spec, PageRequest.of(pageIndex, pageSize, sort));

        return PageResponseDto.<AuditLogEntry>builder()
                .data(paged.getContent())
                .total(paged.getTotalElements())
                .page(pageIndex + 1)
                .limit(pageSize)
                .totalPages(paged.getTotalPages() > 0 ? paged.getTotalPages() : 1)
                .build();
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordLogAsync(AuditLogEntry entry) {
        try {
            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Falha ao persistir audit log de forma assíncrona: {}", e.getMessage(), e);
        }
    }
}

