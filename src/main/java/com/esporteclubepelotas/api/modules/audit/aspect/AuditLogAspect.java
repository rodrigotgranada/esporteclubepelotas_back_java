package com.esporteclubepelotas.api.modules.audit.aspect;

import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;
import com.esporteclubepelotas.api.modules.audit.entity.AuditLogEntry;
import com.esporteclubepelotas.api.modules.audit.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;

    @Around("@annotation(auditLog)")
    public Object handleAuditLog(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            AuditLogEntry.AuditLogEntryBuilder builder = AuditLogEntry.builder()
                    .module(auditLog.module())
                    .action(auditLog.action());

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
                builder.actorId(principal.getId())
                        .actorEmail(principal.getEmail())
                        .actorName(principal.getName())
                        .actorRole(principal.getRole());
            } else if (auth != null && auth.isAuthenticated()) {
                builder.actorEmail(auth.getName());
            }

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                } else if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                builder.ipAddress(ip);
                builder.userAgent(request.getHeader("User-Agent"));
            }

            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                for (Object arg : args) {
                    if (arg instanceof String str && (str.length() == 36 || str.contains("-"))) {
                        builder.entityId(str);
                        break;
                    } else if (arg instanceof java.util.UUID uuid) {
                        builder.entityId(uuid.toString());
                        break;
                    }
                }
            }

            String details = auditLog.description();
            if (details == null || details.isBlank()) {
                details = "Método " + joinPoint.getSignature().getName() + " executado com sucesso.";
            }
            builder.details(details);

            auditLogService.recordLogAsync(builder.build());
        } catch (Exception e) {
            log.warn("Erro silencioso ao capturar audit log no Aspect: {}", e.getMessage());
        }

        return result;
    }
}

