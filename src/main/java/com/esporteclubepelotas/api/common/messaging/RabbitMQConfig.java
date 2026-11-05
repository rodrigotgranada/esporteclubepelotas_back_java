package com.esporteclubepelotas.api.common.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

/**
 * Configuração central da topologia de mensageria AMQP (RabbitMQ / CloudAMQP).
 * Suporta URIs completas amqp:// e amqps:// (TLS/SSL) sem travar a inicialização da API.
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "pelotas.direct.exchange";
    public static final String EMAIL_QUEUE = "pelotas.email.queue";
    public static final String EMAIL_ROUTING_KEY = "pelotas.email.routing";

    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${spring.rabbitmq.addresses:${CLOUDAMQP_URL:amqp://guest:guest@localhost:5672}}") String rabbitAddresses) {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        
        if (rabbitAddresses != null && (rabbitAddresses.startsWith("amqp://") || rabbitAddresses.startsWith("amqps://"))) {
            try {
                log.info("🐰 [RabbitMQ Config] Configurando ConnectionFactory via URI (AMQP/AMQPS)...");
                factory.setUri(URI.create(rabbitAddresses.trim()));
            } catch (Exception ex) {
                log.warn("⚠️ [RabbitMQ Config] Falha ao configurar URI AMQP ({}). Tentando fallback por endereço...", ex.getMessage());
                factory.setAddresses(rabbitAddresses.trim());
            }
        } else if (rabbitAddresses != null && !rabbitAddresses.isBlank()) {
            factory.setAddresses(rabbitAddresses.trim());
        }

        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setIgnoreDeclarationExceptions(true);
        return admin;
    }

    @Bean
    public DirectExchange pelotasExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE).build();
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange pelotasExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(pelotasExchange)
                .with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jacksonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter);
        return template;
    }
}
