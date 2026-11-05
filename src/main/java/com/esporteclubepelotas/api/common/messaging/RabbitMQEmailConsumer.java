package com.esporteclubepelotas.api.common.messaging;

import com.esporteclubepelotas.api.common.email.ResendEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Consumer assíncrono de notificações de e-mail: consome a fila do RabbitMQ
 * e delega o disparo transacional via provedor de e-mail configurado (Resend).
 */
@Slf4j
@Component
public class RabbitMQEmailConsumer {

    private final ResendEmailService resendEmailService;

    @Autowired
    public RabbitMQEmailConsumer(ResendEmailService resendEmailService) {
        this.resendEmailService = resendEmailService;
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consumeEmailMessage(EmailMessagePayload payload) {
        log.info("📥 [RabbitMQ Consumer] Processando envio de e-mail assíncrono para: {} | Assunto: {}",
                payload.to(), payload.subject());
        try {
            resendEmailService.sendHtmlEmail(payload.to(), payload.subject(), payload.htmlContent());
        } catch (Exception ex) {
            log.error("❌ [RabbitMQ Consumer] Erro ao disparar e-mail para {}: {}", payload.to(), ex.getMessage(), ex);
        }
    }
}

