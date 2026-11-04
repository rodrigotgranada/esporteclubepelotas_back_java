package com.esporteclubepelotas.api.common.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ResendEmailService implements EmailService {

    @Value("${resend.api.key:${RESEND_API_KEY:}}")
    private String resendApiKey;

    @Value("${email.from:Esporte Clube Pelotas <atendimento@esporteclubepelotas.com.br>}")
    private String emailFrom;

    private final RestClient restClient;

    public ResendEmailService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .build();
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (resendApiKey == null || resendApiKey.isBlank() || resendApiKey.startsWith("mock")) {
            log.info("📧 [Email Mock - Fallback] Para: {} | Assunto: {} (API Key do Resend não configurada)", to, subject);
            return;
        }

        try {
            Map<String, Object> body = Map.of(
                    "from", emailFrom,
                    "to", List.of(to),
                    "subject", subject,
                    "html", htmlContent
            );

            restClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + resendApiKey.trim())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            log.info("✅ [Resend] E-mail enviado com sucesso para: {}", to);
        } catch (Exception e) {
            log.error("❌ [Resend] Falha ao enviar e-mail para {}: {}", to, e.getMessage(), e);
        }
    }
}

