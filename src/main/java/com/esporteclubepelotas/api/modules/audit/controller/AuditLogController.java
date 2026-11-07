package com.esporteclubepelotas.api.modules.audit.controller;

import com.esporteclubepelotas.api.common.dto.PageResponseDto;
import com.esporteclubepelotas.api.modules.audit.entity.AuditLogEntry;
import com.esporteclubepelotas.api.modules.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/audit-logs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Audit Logs", description = "Trilha de Auditoria e Logs Administrativos")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Listar logs de auditoria com paginação e filtros")
    public ResponseEntity<PageResponseDto<AuditLogEntry>> getLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String actorEmail,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(auditLogService.findLogs(page, limit, module, action, actorEmail, search));
    }
}

