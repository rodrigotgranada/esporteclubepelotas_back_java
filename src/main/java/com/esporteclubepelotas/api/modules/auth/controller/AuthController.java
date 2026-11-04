package com.esporteclubepelotas.api.modules.auth.controller;

import com.esporteclubepelotas.api.common.security.CurrentUser;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.auth.dto.*;
import com.esporteclubepelotas.api.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserSummaryDto> getMe(@CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(authService.getMe(principal.getId()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<UserSummaryDto> changePassword(
            @CurrentUser UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequestDto dto) {
        return ResponseEntity.ok(authService.changePassword(principal.getId(), dto));
    }
}

