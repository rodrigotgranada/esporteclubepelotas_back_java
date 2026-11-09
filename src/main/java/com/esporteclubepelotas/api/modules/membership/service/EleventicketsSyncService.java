package com.esporteclubepelotas.api.modules.membership.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.membership.dto.EleventicketsImportSummaryDto;
import com.esporteclubepelotas.api.modules.membership.dto.EleventicketsMemberDto;
import com.esporteclubepelotas.api.modules.membership.dto.EleventicketsSummaryDto;
import com.esporteclubepelotas.api.modules.membership.dto.ManualSocioOverrideDto;
import com.esporteclubepelotas.api.modules.membership.entity.EleventicketsMemberCache;
import com.esporteclubepelotas.api.modules.membership.repository.EleventicketsMemberCacheRepository;
import com.esporteclubepelotas.api.modules.role.entity.Role;
import com.esporteclubepelotas.api.modules.role.repository.RoleRepository;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EleventicketsSyncService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EleventicketsMemberCacheRepository cacheRepository;
    private final PortalWebSocketHandler webSocketHandler;

    @Transactional
    public EleventicketsImportSummaryDto importCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("O arquivo CSV de sócios não pode estar vazio.");
        }

        EleventicketsImportSummaryDto summary = EleventicketsImportSummaryDto.builder()
                .errorMessages(new ArrayList<>())
                .build();

        Role socioRole = roleRepository.findByName("SOCIO").orElse(null);
        Role userRole = roleRepository.findByName("USER").orElse(null);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                throw new BusinessException("Arquivo CSV vazio ou sem cabeçalho.");
            }

            // Remove BOM do início se presente (comum em Excel)
            if (headerLine.startsWith("\uFEFF")) {
                headerLine = headerLine.substring(1);
            }

            char delimiter = headerLine.contains(";") ? ';' : ',';
            String[] headers = parseCsvLine(headerLine, delimiter);

            Map<String, Integer> colMap = buildColumnMap(headers);
            if (!colMap.containsKey("cpf")) {
                throw new BusinessException("Coluna de CPF não identificada no arquivo CSV. Formatos aceitos: 'cpf', 'documento', 'nr_cpf'.");
            }

            int cpfIdx = colMap.get("cpf");
            Integer idIdx = colMap.get("id");
            Integer nameIdx = colMap.get("name");
            Integer emailIdx = colMap.get("email");
            Integer planIdx = colMap.get("plan");
            Integer statusIdx = colMap.get("status");

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;

                String[] parts = parseCsvLine(line, delimiter);
                if (parts.length <= cpfIdx) {
                    summary.getErrorMessages().add("Linha " + lineNumber + ": Quantidade insuficiente de colunas.");
                    summary.setErrorsCount(summary.getErrorsCount() + 1);
                    continue;
                }

                String rawCpf = parts[cpfIdx];
                String cleanCpf = rawCpf != null ? rawCpf.replaceAll("[^0-9]", "") : "";

                if (cleanCpf.length() < 11) {
                    summary.getErrorMessages().add("Linha " + lineNumber + ": CPF inválido (" + rawCpf + ").");
                    summary.setErrorsCount(summary.getErrorsCount() + 1);
                    continue;
                }

                String eleventicketsId = (idIdx != null && parts.length > idIdx && !parts[idIdx].isBlank())
                        ? parts[idIdx].trim()
                        : cleanCpf;

                String fullName = (nameIdx != null && parts.length > nameIdx) ? parts[nameIdx].trim() : null;
                String email = (emailIdx != null && parts.length > emailIdx) ? parts[emailIdx].trim().toLowerCase() : null;
                String planName = (planIdx != null && parts.length > planIdx && !parts[planIdx].isBlank())
                        ? parts[planIdx].trim()
                        : "Sócio Áureo-Cerúleo";

                String rawStatus = (statusIdx != null && parts.length > statusIdx) ? parts[statusIdx].trim() : "ACTIVE";
                String normalizedStatus = normalizeSocioStatus(rawStatus);
                boolean isActive = "ACTIVE".equals(normalizedStatus);

                summary.setTotalRows(summary.getTotalRows() + 1);

                // 1. Verifica se usuário já existe no portal
                Optional<User> existingUserOpt = userRepository.findByCpf(cleanCpf);
                if (existingUserOpt.isPresent()) {
                    User user = existingUserOpt.get();

                    // Se possui trava manual configurada pelo Admin, respeita
                    if ("MANUAL_OVERRIDE".equals(user.getSocioSyncMode())) {
                        summary.setManualOverridesPreserved(summary.getManualOverridesPreserved() + 1);
                        log.info("ℹ️ Trava manual preservada para o torcedor CPF: {}", cleanCpf);
                        continue;
                    }

                    user.setEleventicketsId(eleventicketsId);
                    user.setSocioPlanName(planName);
                    user.setSocioStatus(normalizedStatus);
                    user.setSocioLastSyncedAt(Instant.now());

                    if (isActive) {
                        user.setSocio(true);
                        if (user.getSocioSince() == null) {
                            user.setSocioSince(LocalDate.now());
                        }
                        if (user.getRole() != null && "USER".equals(user.getRole().getName()) && socioRole != null) {
                            user.setRole(socioRole);
                        }
                        summary.setUsersActivated(summary.getUsersActivated() + 1);
                    } else {
                        user.setSocio(false);
                        if (user.getRole() != null && "SOCIO".equals(user.getRole().getName()) && userRole != null) {
                            user.setRole(userRole);
                        }
                        summary.setUsersDeactivated(summary.getUsersDeactivated() + 1);
                    }

                    userRepository.save(user);
                    summary.setUsersUpdated(summary.getUsersUpdated() + 1);
                } else {
                    // 2. Torcedor ainda não cadastrado no portal -> guarda no cache
                    EleventicketsMemberCache cacheItem = cacheRepository.findByCpf(cleanCpf)
                            .orElse(EleventicketsMemberCache.builder().cpf(cleanCpf).build());

                    cacheItem.setEleventicketsId(eleventicketsId);
                    cacheItem.setFullName(fullName);
                    cacheItem.setEmail(email);
                    cacheItem.setPlanName(planName);
                    cacheItem.setStatus(normalizedStatus);

                    cacheRepository.save(cacheItem);
                    summary.setCacheInserted(summary.getCacheInserted() + 1);
                }
            }

            webSocketHandler.broadcastAfterCommit("membership_sync", "IMPORTED", "summary");
            log.info("✅ Importação ElevenTickets concluída: {} processados, {} atualizados no portal, {} no cache.",
                    summary.getTotalRows(), summary.getUsersUpdated(), summary.getCacheInserted());

            return summary;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Erro ao processar arquivo CSV da ElevenTickets", e);
            throw new BusinessException("Falha ao processar arquivo CSV: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public EleventicketsSummaryDto getSummary() {
        long active = userRepository.countByIsSocioTrue();
        long inactive = userRepository.countByIsSocioFalseAndEleventicketsIdIsNotNull();
        long overrides = userRepository.countBySocioSyncMode("MANUAL_OVERRIDE");
        long cachePending = cacheRepository.count();

        return EleventicketsSummaryDto.builder()
                .totalActiveMembers(active)
                .totalInactiveMembers(inactive)
                .totalManualOverrides(overrides)
                .totalCachePending(cachePending)
                .lastImportedAt(Instant.now())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<EleventicketsMemberDto> getMembers(
            String search,
            Boolean isSocio,
            String status,
            String syncMode,
            Pageable pageable
    ) {
        String cleanSearch = (search != null && !search.isBlank()) ? search.trim().toLowerCase() : null;
        String cleanStatus = (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) ? status.trim().toUpperCase() : null;
        String cleanSyncMode = (syncMode != null && !syncMode.isBlank() && !"ALL".equalsIgnoreCase(syncMode)) ? syncMode.trim().toUpperCase() : null;

        Page<User> usersPage = userRepository.findMembersWithFilters(
                cleanSearch,
                isSocio,
                cleanStatus,
                cleanSyncMode,
                pageable
        );

        return usersPage.map(this::toMemberDto);
    }

    @Transactional
    public EleventicketsMemberDto manualOverride(String userId, ManualSocioOverrideDto dto, String adminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        Role socioRole = roleRepository.findByName("SOCIO").orElse(null);
        Role userRole = roleRepository.findByName("USER").orElse(null);

        user.setSocio(dto.isSocio());
        user.setSocioStatus(dto.getStatus() != null ? dto.getStatus() : (dto.isSocio() ? "ACTIVE" : "INACTIVE"));
        user.setSocioSyncMode("MANUAL_OVERRIDE");
        user.setSocioOverrideReason(dto.getReason() + " (por " + adminEmail + ")");
        user.setSocioOverrideAt(Instant.now());

        if (dto.isSocio()) {
            if (user.getSocioSince() == null) {
                user.setSocioSince(LocalDate.now());
            }
            if (user.getRole() != null && "USER".equals(user.getRole().getName()) && socioRole != null) {
                user.setRole(socioRole);
            }
        } else {
            if (user.getRole() != null && "SOCIO".equals(user.getRole().getName()) && userRole != null) {
                user.setRole(userRole);
            }
        }

        User saved = userRepository.save(user);
        webSocketHandler.broadcastAfterCommit("membership_sync", "OVERRIDDEN", saved.getId());

        log.info("🔒 Trava manual de sócio aplicada em {} por {}: isSocio={}", user.getEmail(), adminEmail, dto.isSocio());
        return toMemberDto(saved);
    }

    @Transactional
    public EleventicketsMemberDto releaseOverride(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        user.setSocioSyncMode("AUTO");
        user.setSocioOverrideReason(null);
        user.setSocioOverrideAt(null);

        User saved = userRepository.save(user);
        webSocketHandler.broadcastAfterCommit("membership_sync", "RELEASED", saved.getId());

        log.info("🔓 Trava manual removida para {} (restaurado modo AUTO)", user.getEmail());
        return toMemberDto(saved);
    }

    private EleventicketsMemberDto toMemberDto(User user) {
        return EleventicketsMemberDto.builder()
                .userId(user.getId())
                .name(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .cpf(user.getCpf())
                .avatarUrl(user.getAvatarUrl())
                .roleName(user.getRole() != null ? user.getRole().getName() : "USER")
                .eleventicketsId(user.getEleventicketsId())
                .isSocio(user.isSocio())
                .planName(user.getSocioPlanName())
                .status(user.getSocioStatus())
                .syncMode(user.getSocioSyncMode())
                .overrideReason(user.getSocioOverrideReason())
                .overrideAt(user.getSocioOverrideAt())
                .socioSince(user.getSocioSince())
                .lastSyncedAt(user.getSocioLastSyncedAt())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private Map<String, Integer> buildColumnMap(String[] headers) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            String h = headers[i].trim().toLowerCase().replaceAll("[^a-z0-9]", "");
            if (h.contains("cpf") || h.contains("documento")) {
                map.put("cpf", i);
            } else if (h.equals("id") || h.contains("matricula") || h.contains("codigo") || h.contains("eleven")) {
                map.put("id", i);
            } else if (h.contains("nome") || h.contains("name") || h.contains("socio")) {
                map.put("name", i);
            } else if (h.contains("email") || h.contains("mail")) {
                map.put("email", i);
            } else if (h.contains("plano") || h.contains("categoria") || h.contains("plan")) {
                map.put("plan", i);
            } else if (h.contains("status") || h.contains("situacao") || h.contains("ativo") || h.contains("adimplente")) {
                map.put("status", i);
            }
        }
        return map;
    }

    private String normalizeSocioStatus(String raw) {
        if (raw == null) return "ACTIVE";
        String lower = raw.trim().toLowerCase();
        if (lower.contains("ativo") || lower.contains("active") || lower.contains("pago") || lower.contains("adimplente") || lower.equals("1") || lower.equals("sim")) {
            return "ACTIVE";
        }
        if (lower.contains("inadimplente") || lower.contains("atrasado") || lower.contains("overdue") || lower.contains("pendente")) {
            return "OVERDUE";
        }
        if (lower.contains("suspenso") || lower.contains("suspended")) {
            return "SUSPENDED";
        }
        return "INACTIVE";
    }

    private String[] parseCsvLine(String line, char delimiter) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == delimiter && !inQuotes) {
                tokens.add(sb.toString().trim().replaceAll("^\"|\"$", ""));
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim().replaceAll("^\"|\"$", ""));
        return tokens.toArray(new String[0]);
    }
}
