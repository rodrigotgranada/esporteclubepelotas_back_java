package com.esporteclubepelotas.api.modules.user.controller;

import com.esporteclubepelotas.api.common.dto.PageResponseDto;
import com.esporteclubepelotas.api.modules.auth.dto.UserSummaryDto;
import com.esporteclubepelotas.api.modules.user.service.UserAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserAdminService userAdminService;

    @GetMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<PageResponseDto<UserSummaryDto>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int limit,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(userAdminService.listUsers(page, limit, search));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<UserSummaryDto> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userAdminService.getUserById(id));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserSummaryDto> updateRole(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        String role = payload.get("role");
        return ResponseEntity.ok(userAdminService.updateUserRole(id, role));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<UserSummaryDto> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        return ResponseEntity.ok(userAdminService.updateUserStatus(id, status));
    }
}

