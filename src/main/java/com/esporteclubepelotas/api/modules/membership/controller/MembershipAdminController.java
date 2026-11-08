package com.esporteclubepelotas.api.modules.membership.controller;

import com.esporteclubepelotas.api.common.security.CurrentUser;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;
import com.esporteclubepelotas.api.modules.membership.dto.*;
import com.esporteclubepelotas.api.modules.membership.service.MembershipLeadService;
import com.esporteclubepelotas.api.modules.membership.service.MembershipPlanService;
import com.esporteclubepelotas.api.modules.setting.dto.SettingUpdateDto;
import com.esporteclubepelotas.api.modules.setting.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/membership")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('membership.manage') or hasRole('ADMIN') or hasRole('OWNER') or hasRole('EDITOR')")
@Tag(name = "Planos de Sócios (Administração)", description = "Gestão de planos, vantagens, destaques e leads de adesão do Quadro Social")
public class MembershipAdminController {

    private final MembershipPlanService planService;
    private final MembershipLeadService leadService;
    private final SettingService settingService;
    private final PortalWebSocketHandler webSocketHandler;

    @GetMapping("/system-config")
    @Operation(summary = "Obter provedor ativo do Quadro Social (ElevenTickets ou Nativo)")
    public ResponseEntity<MembershipSystemConfigDto> getSystemConfig() {
        var settings = settingService.getSettings();
        return ResponseEntity.ok(MembershipSystemConfigDto.builder()
                .provider(settings.getMembershipSystemProvider() != null ? settings.getMembershipSystemProvider() : "ELEVENTICKETS")
                .externalUrl(settings.getMembershipExternalUrl() != null ? settings.getMembershipExternalUrl() : "https://socioecpelotas.eleventickets.com/#/")
                .build());
    }

    @PutMapping("/system-config")
    @AuditLog(action = "UPDATE_MEMBERSHIP_SYSTEM_CONFIG", module = "membership")
    @Operation(summary = "Alternar provedor ativo do Quadro Social entre ElevenTickets e Nativo")
    public ResponseEntity<MembershipSystemConfigDto> updateSystemConfig(
            @RequestBody @Valid MembershipSystemConfigDto dto,
            @CurrentUser UserPrincipal user
    ) {
        String provider = (dto.getProvider() != null && !dto.getProvider().isBlank())
                ? dto.getProvider().trim().toUpperCase()
                : "ELEVENTICKETS";
        String externalUrl = (dto.getExternalUrl() != null && !dto.getExternalUrl().isBlank())
                ? dto.getExternalUrl().trim()
                : "https://socioecpelotas.eleventickets.com/#/";

        SettingUpdateDto updateDto = SettingUpdateDto.builder()
                .membershipSystemProvider(provider)
                .membershipExternalUrl(externalUrl)
                .build();

        settingService.updateSettings(updateDto, null);
        webSocketHandler.broadcastAfterCommit("membership_config", "UPDATED", provider);

        return ResponseEntity.ok(MembershipSystemConfigDto.builder()
                .provider(provider)
                .externalUrl(externalUrl)
                .build());
    }

    @GetMapping("/plans")
    @Operation(summary = "Listar todos os planos de sócios para a tabela administrativa")
    public ResponseEntity<List<MembershipPlanDto>> findAllPlans() {
        return ResponseEntity.ok(planService.findAdminPlans());
    }

    @GetMapping("/plans/{id}")
    @Operation(summary = "Buscar plano por ID para edição no Drawer")
    public ResponseEntity<MembershipPlanDto> findPlanById(@PathVariable UUID id) {
        return ResponseEntity.ok(planService.findById(id));
    }

    @PostMapping("/plans")
    @AuditLog(action = "CREATE_MEMBERSHIP_PLAN", module = "membership")
    @Operation(summary = "Cadastrar novo plano de sócio")
    public ResponseEntity<MembershipPlanDto> createPlan(
            @RequestBody @Valid CreateMembershipPlanDto dto,
            @CurrentUser UserPrincipal user
    ) {
        String userId = user != null ? user.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.create(dto, userId));
    }

    @PutMapping("/plans/{id}")
    @AuditLog(action = "UPDATE_MEMBERSHIP_PLAN", module = "membership")
    @Operation(summary = "Atualizar dados e vantagens de um plano existente")
    public ResponseEntity<MembershipPlanDto> updatePlan(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateMembershipPlanDto dto,
            @CurrentUser UserPrincipal user
    ) {
        String userId = user != null ? user.getId() : null;
        return ResponseEntity.ok(planService.update(id, dto, userId));
    }

    @PatchMapping("/plans/{id}/toggle-active")
    @AuditLog(action = "TOGGLE_MEMBERSHIP_PLAN_STATUS", module = "membership")
    @Operation(summary = "Ativar ou pausar a oferta de um plano no portal")
    public ResponseEntity<MembershipPlanDto> toggleActive(
            @PathVariable UUID id,
            @CurrentUser UserPrincipal user
    ) {
        String userId = user != null ? user.getId() : null;
        return ResponseEntity.ok(planService.toggleActive(id, userId));
    }

    @PatchMapping("/plans/reorder")
    @AuditLog(action = "REORDER_MEMBERSHIP_PLANS", module = "membership")
    @Operation(summary = "Reordenar posições de exibição dos planos")
    public ResponseEntity<Map<String, Object>> reorderPlans(
            @RequestBody @Valid ReorderMembershipPlansDto dto,
            @CurrentUser UserPrincipal user
    ) {
        String userId = user != null ? user.getId() : null;
        planService.reorder(dto.getPlanIds(), userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Planos reordenados com sucesso."));
    }

    @DeleteMapping("/plans/{id}")
    @AuditLog(action = "DELETE_MEMBERSHIP_PLAN", module = "membership")
    @Operation(summary = "Inativar plano via soft-delete permanente")
    public ResponseEntity<Map<String, Object>> deletePlan(
            @PathVariable UUID id,
            @CurrentUser UserPrincipal user
    ) {
        String userId = user != null ? user.getId() : null;
        planService.delete(id, userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Plano excluído com sucesso."));
    }

    @GetMapping("/leads")
    @Operation(summary = "Listar torcedores interessados (leads) com filtro de status e paginação")
    public ResponseEntity<Page<MembershipLeadDto>> findLeads(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(leadService.findPaginated(status, page, limit));
    }

    @PatchMapping("/leads/{id}/status")
    @AuditLog(action = "UPDATE_MEMBERSHIP_LEAD_STATUS", module = "membership")
    @Operation(summary = "Atualizar status de atendimento do torcedor (NOVO, CONTATADO, CONVERTIDO, CANCELADO)")
    public ResponseEntity<MembershipLeadDto> updateLeadStatus(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateMembershipLeadStatusDto dto,
            @CurrentUser UserPrincipal user
    ) {
        String userId = user != null ? user.getId() : null;
        return ResponseEntity.ok(leadService.updateStatus(id, dto, userId));
    }
}

