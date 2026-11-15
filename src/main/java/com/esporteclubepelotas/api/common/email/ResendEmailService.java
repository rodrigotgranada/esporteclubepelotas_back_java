package com.esporteclubepelotas.api.common.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Primary
@Service
public class ResendEmailService implements EmailService {

    @Value("${resend.api-key:${RESEND_API_KEY:}}")
    private String resendApiKey;

    @Value("${email.from:Esporte Clube Pelotas <atendimento@esporteclubepelotas.com.br>}")
    private String emailFrom;

    private final RestClient restClient;

    public ResendEmailService() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .requestFactory(requestFactory)
                .build();
    }

    @Async("taskExecutor")
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000L, multiplier = 2.0)
    )
    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (resendApiKey == null || resendApiKey.isBlank() || resendApiKey.startsWith("mock")) {
            log.info("📧 [Email Mock - Fallback] Para: {} | Assunto: {} (API Key do Resend não configurada)", to, subject);
            return;
        }

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
    }

    @Recover
    public void recoverSendHtmlEmail(Exception ex, String to, String subject, String htmlContent) {
        log.error("❌ [Resend] Falha definitiva após retentativas ao enviar e-mail para {} | Assunto: {} | Erro: {}",
                to, subject, ex.getMessage(), ex);
    }
}
