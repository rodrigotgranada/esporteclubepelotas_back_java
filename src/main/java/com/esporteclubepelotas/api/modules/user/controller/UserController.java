package com.esporteclubepelotas.api.modules.user.controller;

import com.esporteclubepelotas.api.common.dto.PageResponseDto;
import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.security.CurrentUser;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;
import com.esporteclubepelotas.api.modules.auth.dto.UserSummaryDto;
import com.esporteclubepelotas.api.modules.auth.service.AuthService;
import com.esporteclubepelotas.api.modules.user.dto.AdminUpdateUserDto;
import com.esporteclubepelotas.api.modules.user.dto.CheckAvailabilityResponseDto;
import com.esporteclubepelotas.api.modules.user.dto.CreateUserDto;
import com.esporteclubepelotas.api.modules.user.service.UserAdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestão de Usuários e Administração de Contas")
public class UserController {

    private final UserAdminService userAdminService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR')")
    @Operation(summary = "Listar usuários com filtros e paginação")
    public ResponseEntity<PageResponseDto<UserSummaryDto>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(userAdminService.listUsers(page, limit, search, role, status));
    }

    @GetMapping("/check-availability")
    @Operation(summary = "Verificar disponibilidade de e-mail ou CPF")
    public ResponseEntity<CheckAvailabilityResponseDto> checkAvailability(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String value) {
        if (type != null && value != null) {
            if ("email".equalsIgnoreCase(type)) email = value;
            else if ("cpf".equalsIgnoreCase(type)) cpf = value;
        }
        return ResponseEntity.ok(userAdminService.checkAvailability(email, cpf));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR')")
    @Operation(summary = "Obter dados completos de um usuário por ID")
    public ResponseEntity<UserSummaryDto> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userAdminService.getUserById(id));
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cadastro público de novos torcedores/sócios com avatar opcional")
    public ResponseEntity<UserSummaryDto> register(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        CreateUserDto dto = parseJson(dataJson, CreateUserDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto, avatar));
    }

    @PostMapping(value = "/admin/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Upload prévio de foto de perfil antes de persistir os dados cadastrais do usuário")
    public ResponseEntity<Map<String, String>> uploadStandaloneAvatar(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam(value = "folderId", required = false) String folderId) {
        MultipartFile targetFile = file != null ? file : avatar;
        return ResponseEntity.ok(userAdminService.uploadStandaloneAvatar(targetFile, folderId));
    }

    @PostMapping(value = "/admin/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @AuditLog(module = "users", action = "CREATE_USER", description = "Criação de usuário via painel administrativo")
    @Operation(summary = "Criação direta de usuário pelo Painel Administrativo")
    public ResponseEntity<UserSummaryDto> adminCreate(
            @RequestPart("data") String dataJson,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        CreateUserDto dto = parseJson(dataJson, CreateUserDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(userAdminService.adminCreate(dto, avatar));
    }

    @PatchMapping("/admin/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @AuditLog(module = "users", action = "UPDATE_USER", description = "Edição cadastral de usuário")
    @Operation(summary = "Edição cadastral de usuário pelo Administrador")
    public ResponseEntity<UserSummaryDto> adminUpdate(
            @PathVariable String id,
            @RequestBody AdminUpdateUserDto dto,
            @CurrentUser UserPrincipal actor) {
        return ResponseEntity.ok(userAdminService.adminUpdate(id, dto, actor.getRole()));
    }

    @PostMapping(value = "/admin/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @AuditLog(module = "users", action = "UPDATE_AVATAR", description = "Alteração de foto de perfil de usuário pelo Admin")
    @Operation(summary = "Upload de foto de perfil de qualquer usuário pelo Admin")
    public ResponseEntity<Map<String, String>> adminUpdateAvatar(
            @PathVariable String id,
            @RequestPart("avatar") MultipartFile avatar,
            @CurrentUser UserPrincipal actor) {
        return ResponseEntity.ok(userAdminService.adminUpdateAvatar(id, avatar, actor.getRole()));
    }

    @DeleteMapping("/admin/{id}/avatar")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @AuditLog(module = "users", action = "DELETE_AVATAR", description = "Remoção de foto de perfil de usuário pelo Admin")
    @Operation(summary = "Remoção de foto de perfil de um usuário pelo Admin")
    public ResponseEntity<UserSummaryDto> adminDeleteAvatar(
            @PathVariable String id,
            @CurrentUser UserPrincipal actor) {
        return ResponseEntity.ok(userAdminService.adminDeleteAvatar(id, actor.getRole()));
    }

    @PatchMapping({"/admin/{id}/role", "/{id}/role"})
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @AuditLog(module = "users", action = "CHANGE_ROLE", description = "Alteração de perfil/role de usuário")
    @Operation(summary = "Alteração de cargo/role de um usuário")
    public ResponseEntity<UserSummaryDto> updateRole(
            @PathVariable String id,
            @RequestBody Map<String, String> payload,
            @CurrentUser UserPrincipal actor) {
        String role = payload.get("role");
        return ResponseEntity.ok(userAdminService.updateUserRole(id, role, actor.getRole()));
    }

    @PatchMapping({"/admin/{id}/status", "/{id}/status"})
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @AuditLog(module = "users", action = "CHANGE_STATUS", description = "Alteração de status do usuário (ativo/inativo)")
    @Operation(summary = "Alteração de status (ativo, bloqueado, suspenso) de um usuário")
    public ResponseEntity<UserSummaryDto> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload,
            @CurrentUser UserPrincipal actor) {
        String status = payload.get("status");
        return ResponseEntity.ok(userAdminService.updateUserStatus(id, status, actor.getRole()));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @AuditLog(module = "users", action = "DELETE_USER", description = "Exclusão lógica (soft-delete) de usuário")
    @Operation(summary = "Exclusão lógica (soft-delete) de usuário")
    public ResponseEntity<Map<String, Boolean>> adminSoftDelete(
            @PathVariable String id,
            @CurrentUser UserPrincipal actor) {
        userAdminService.adminSoftDelete(id, actor.getRole());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/admin/{id}/resend-verification")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @AuditLog(module = "users", action = "RESEND_VERIFICATION", description = "Reenvio de código de ativação pelo Admin")
    @Operation(summary = "Reenviar e-mail com código de ativação pelo Admin")
    public ResponseEntity<Map<String, Boolean>> adminResendVerification(
            @PathVariable String id,
            @CurrentUser UserPrincipal actor) {
        userAdminService.adminResendVerification(id, actor.getRole());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/admin/{id}/force-password-reset")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @AuditLog(module = "users", action = "FORCE_PASSWORD_RESET", description = "Disparo de redefinição de senha pelo Admin")
    @Operation(summary = "Disparar e-mail de redefinição forçada de senha pelo Admin")
    public ResponseEntity<Map<String, Boolean>> adminForcePasswordReset(
            @PathVariable String id,
            @CurrentUser UserPrincipal actor) {
        userAdminService.adminForcePasswordReset(id, actor.getRole());
        return ResponseEntity.ok(Map.of("success", true));
    }

    private <T> T parseJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Erro ao converter payload JSON: {}", json, e);
            throw new BusinessException("Formato JSON inválido no campo 'data'");
        }
    }
}
