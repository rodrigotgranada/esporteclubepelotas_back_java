package com.esporteclubepelotas.api.modules.auth.controller;

import com.esporteclubepelotas.api.common.security.CurrentUser;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.auth.dto.*;
import com.esporteclubepelotas.api.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticação, Tokens e Recuperação de Acesso")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário por CPF ou E-mail")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletResponse response) {
        AuthResponseDto result = authService.login(request);
        setRefreshTokenCookie(response, result.getRefreshToken());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/verify")
    @Operation(summary = "Validar e-mail com código OTP de 6 dígitos")
    public ResponseEntity<AuthResponseDto> verifyEmail(
            @Valid @RequestBody VerifyEmailRequestDto request,
            HttpServletResponse response) {
        AuthResponseDto result = authService.verifyEmail(request);
        setRefreshTokenCookie(response, result.getRefreshToken());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/resend-code")
    @Operation(summary = "Reenviar código de confirmação de e-mail")
    public ResponseEntity<Map<String, Boolean>> resendCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        authService.resendCode(email);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperação de senha por e-mail")
    public ResponseEntity<Map<String, Boolean>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefinir senha com token de recuperação")
    public ResponseEntity<Map<String, Boolean>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar Access Token com Refresh Token")
    public ResponseEntity<AuthResponseDto> refresh(
            @RequestBody(required = false) RefreshTokenRequestDto request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String token = (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isBlank())
                ? request.getRefreshToken()
                : extractRefreshTokenFromCookies(httpRequest);

        if (token == null || token.isBlank()) {
            return ResponseEntity.status(401).build();
        }

        AuthResponseDto result = authService.refreshToken(new RefreshTokenRequestDto(token));
        setRefreshTokenCookie(httpResponse, result.getRefreshToken());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do perfil do usuário autenticado")
    public ResponseEntity<UserSummaryDto> getMe(@CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(authService.getMe(principal.getId()));
    }

    @GetMapping("/env-test")
    @Operation(summary = "Diagnóstico público de ambiente e conexões para o EnvironmentBadge")
    public ResponseEntity<Map<String, Object>> envTest() {
        return ResponseEntity.ok(Map.of(
                "stage", "DEV",
                "environment", "development",
                "databaseSuffix", "ecpelotas_postgres",
                "mongoConnected", true,
                "storageProvider", "firebase",
                "storageProject", "esporteclubepelotasdev",
                "storageConfigured", true
        ));
    }

    @PostMapping({"/change-password", "/change-temporary-password"})
    @Operation(summary = "Alteração obrigatória de senha provisória")
    public ResponseEntity<UserSummaryDto> changePassword(
            @CurrentUser UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequestDto dto) {
        return ResponseEntity.ok(authService.changePassword(principal.getId(), dto));
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        if (refreshToken == null) return;
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // em dev/prod HTTPS o proxy Render cuida ou secure se configurado
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if ("refresh_token".equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
