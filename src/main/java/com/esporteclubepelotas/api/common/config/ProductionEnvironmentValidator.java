package com.esporteclubepelotas.api.common.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Profile("prod")
public class ProductionEnvironmentValidator {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${firebase.project-id}")
    private String firebaseProjectId;

    @Value("${firebase.storage-bucket}")
    private String firebaseStorageBucket;

    @Value("${firebase.credentials-base64}")
    private String firebaseCredentialsBase64;

    @Value("${resend.api-key}")
    private String resendApiKey;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${cors.allowed-origins}")
    private String corsAllowedOrigins;

    @PostConstruct
    public void validate() {
        List<String> errors = new ArrayList<>();

        if (jwtSecret == null || jwtSecret.isBlank() || jwtSecret.trim().length() < 32) {
            errors.add("JWT_SECRET ausente ou menor que 32 caracteres");
        }
        if (firebaseProjectId == null || firebaseProjectId.isBlank()) {
            errors.add("FIREBASE_PROJECT_ID ausente ou vazio");
        }
        if (firebaseStorageBucket == null || firebaseStorageBucket.isBlank()) {
            errors.add("FIREBASE_STORAGE_BUCKET ausente ou vazio");
        }
        if (firebaseCredentialsBase64 == null || firebaseCredentialsBase64.isBlank()) {
            errors.add("FIREBASE_CREDENTIALS_BASE64 ausente ou vazio");
        }
        if (resendApiKey == null || resendApiKey.isBlank() || resendApiKey.trim().startsWith("mock")) {
            errors.add("RESEND_API_KEY ausente ou inválida");
        }
        if (frontendUrl == null || frontendUrl.isBlank()) {
            errors.add("FRONTEND_URL ausente ou vazio");
        }
        if (corsAllowedOrigins == null || corsAllowedOrigins.isBlank()) {
            errors.add("CORS_ALLOWED_ORIGINS ausente ou vazio");
        }

        if (!errors.isEmpty()) {
            String message = "Boot bloqueado — variáveis obrigatórias de produção inválidas/ausentes: "
                    + String.join("; ", errors);
            log.error("🚨 [Prod Validator] {}", message);
            throw new IllegalStateException(message);
        }

        log.info("🛡️ [Prod Validator] Variáveis críticas de produção validadas com sucesso.");
    }
}

