package com.esporteclubepelotas.api.common.messaging;

import com.esporteclubepelotas.api.common.email.EmailService;
import com.esporteclubepelotas.api.common.email.ResendEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Producer primário de e-mails: publica o payload na fila do RabbitMQ de forma assíncrona.
 * Possui mecanismo de fallback resiliente para envio direto caso o broker AMQP esteja indisponível.
 */
@Slf4j
@Primary
@Service
public class RabbitMQEmailProducer implements EmailService {

    private final RabbitTemplate rabbitTemplate;
    private final ResendEmailService resendEmailService;

    @Value("${spring.rabbitmq.listener.simple.auto-startup:true}")
    private boolean rabbitMqEnabled;

    @Autowired
    public RabbitMQEmailProducer(RabbitTemplate rabbitTemplate, ResendEmailService resendEmailService) {
        this.rabbitTemplate = rabbitTemplate;
        this.resendEmailService = resendEmailService;
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (!rabbitMqEnabled) {
            log.info("⚠️ [RabbitMQ Desativado] Enviando e-mail de forma síncrona/direta para: {}", to);
            resendEmailService.sendHtmlEmail(to, subject, htmlContent);
            return;
        }

        try {
            EmailMessagePayload payload = EmailMessagePayload.of(to, subject, htmlContent);
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.EMAIL_ROUTING_KEY, payload);
            log.info("📬 [RabbitMQ Producer] Mensagem de e-mail postada na fila '{}' para: {}", RabbitMQConfig.EMAIL_QUEUE, to);
        } catch (Exception ex) {
            log.warn("⚠️ [RabbitMQ Fallback] Falha ao publicar na fila AMQP ({}). Executando envio direto via Resend...", ex.getMessage());
            resendEmailService.sendHtmlEmail(to, subject, htmlContent);
        }
    }
}

