package com.esporteclubepelotas.api.modules.auth.controller;

import com.esporteclubepelotas.api.common.security.CurrentUser;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.auth.dto.AuthResponseDto;
import com.esporteclubepelotas.api.modules.auth.dto.ChangePasswordRequestDto;
import com.esporteclubepelotas.api.modules.auth.dto.ForgotPasswordRequestDto;
import com.esporteclubepelotas.api.modules.auth.dto.LoginRequestDto;
import com.esporteclubepelotas.api.modules.auth.dto.RefreshTokenRequestDto;
import com.esporteclubepelotas.api.modules.auth.dto.ResetPasswordRequestDto;
import com.esporteclubepelotas.api.modules.auth.dto.UserSummaryDto;
import com.esporteclubepelotas.api.modules.auth.dto.VerifyEmailRequestDto;
import com.esporteclubepelotas.api.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Autenticação, Tokens e Recuperação de Acesso")
public class AuthController {

    private final AuthService authService;
    private final Environment environment;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site:Lax}")
    private String cookieSameSite;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    public AuthController(AuthService authService, Environment environment) {
        this.authService = authService;
        this.environment = environment;
    }

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
    public ResponseEntity<Map<String, Boolean>> resendCode(@RequestBody(required = false) Map<String, String> payload) {
        String email = payload != null ? payload.get("email") : null;
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

    @PostMapping("/logout")
    @Operation(summary = "Encerrar sessão limpando o cookie de Refresh Token")
    public ResponseEntity<Map<String, Boolean>> logout(HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite != null && !cookieSameSite.isBlank() ? cookieSameSite.trim() : "Lax");

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            cookieBuilder.domain(cookieDomain.trim());
        }

        response.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do perfil do usuário autenticado")
    public ResponseEntity<UserSummaryDto> getMe(@CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(authService.getMe(principal.getId()));
    }

    @GetMapping("/env-test")
    @Operation(summary = "Diagnóstico mínimo de estágio de ambiente para o EnvironmentBadge")
    public ResponseEntity<Map<String, Object>> envTest() {
        boolean isProd = resolveIsProdStage();
        return ResponseEntity.ok(Map.of(
                "stage", isProd ? "PROD" : "DEV",
                "environment", isProd ? "production" : "development",
                "storageProvider", "firebase"
        ));
    }

    private boolean resolveIsProdStage() {
        String explicitStage = firstNonBlank(
                environment.getProperty("app.stage"),
                environment.getProperty("APP_STAGE"),
                environment.getProperty("app.env"),
                environment.getProperty("APP_ENV"));
        if (explicitStage != null) {
            String normalized = explicitStage.trim().toLowerCase(Locale.ROOT);
            if ("prod".equals(normalized) || "production".equals(normalized)) {
                return true;
            }
            if ("dev".equals(normalized) || "development".equals(normalized)
                    || "staging".equals(normalized) || "homolog".equals(normalized)
                    || "preview".equals(normalized) || "local".equals(normalized)
                    || "test".equals(normalized)) {
                return false;
            }
        }

        String gitBranch = firstNonBlank(
                environment.getProperty("RENDER_GIT_BRANCH"),
                environment.getProperty("GIT_BRANCH"));
        if (gitBranch != null) {
            String branchLower = gitBranch.trim().toLowerCase(Locale.ROOT);
            if (!"main".equals(branchLower) && !"master".equals(branchLower)) {
                return false;
            }
        }

        String renderService = firstNonBlank(
                environment.getProperty("RENDER_SERVICE_NAME"),
                environment.getProperty("RENDER_EXTERNAL_HOSTNAME"));
        if (containsAnyMarker(renderService, "-dev", "dev-", "develop", "homolog", "staging", "preview")) {
            return false;
        }

        boolean hasProdProfile = environment.acceptsProfiles(Profiles.of("prod"))
                || (activeProfile != null && activeProfile.toLowerCase(Locale.ROOT).contains("prod"));
        if (!hasProdProfile) {
            return false;
        }

        String firebaseProject = firstNonBlank(
                environment.getProperty("firebase.project-id"),
                environment.getProperty("firebase.storage-bucket"));
        if (containsAnyMarker(firebaseProject, "dev", "homolog", "staging", "local", "test")) {
            return false;
        }

        String frontendUrl = environment.getProperty("frontend.url");
        if (containsAnyMarker(frontendUrl,
                "localhost", "127.0.0.1", "dev.", "-dev", "_dev", "-git-develop", "homolog", "staging")) {
            return false;
        }

        String datasourceUrl = environment.getProperty("spring.datasource.url");
        return !containsAnyMarker(datasourceUrl,
                "localhost", "127.0.0.1", "ecpelotas_local", "ecp_dev", "_dev", "-dev", "homolog", "staging");
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private static boolean containsAnyMarker(String value, String... markers) {
        if (value == null || value.isBlank()) return false;
        String lower = value.toLowerCase(Locale.ROOT);
        for (String marker : markers) {
            if (lower.contains(marker)) {
                return true;
            }
        }
        return false;
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
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite(cookieSameSite != null && !cookieSameSite.isBlank() ? cookieSameSite.trim() : "Lax");

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            cookieBuilder.domain(cookieDomain.trim());
        }

        response.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());
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
