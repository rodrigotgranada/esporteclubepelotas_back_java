package com.esporteclubepelotas.api.common.messaging;

import java.io.Serializable;
import java.time.Instant;

/**
 * Payload imutável para transporte de notificações de e-mail via fila AMQP / RabbitMQ.
 */
public record EmailMessagePayload(
        String to,
        String subject,
        String htmlContent,
        Instant timestamp
) implements Serializable {

    public static EmailMessagePayload of(String to, String subject, String htmlContent) {
        return new EmailMessagePayload(to, subject, htmlContent, Instant.now());
    }
}

