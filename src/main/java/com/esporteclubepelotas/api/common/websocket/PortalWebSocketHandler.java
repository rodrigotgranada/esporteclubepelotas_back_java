package com.esporteclubepelotas.api.common.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class PortalWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.debug("[WebSocket] Sessão conectada: {} (ativas: {})", session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if ("PING".equalsIgnoreCase(payload != null ? payload.trim() : "")) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage("PONG"));
                }
            } catch (IOException ignored) {
                sessions.remove(session);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.debug("[WebSocket] Sessão encerrada: {} (ativas: {})", session.getId(), sessions.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        sessions.remove(session);
        try {
            if (session.isOpen()) {
                session.close(CloseStatus.SERVER_ERROR);
            }
        } catch (IOException ignored) {
        }
    }

    public void broadcastAfterCommit(String topic, String action, String entityId, Boolean isYouth) {
        RealtimeEventDto event = RealtimeEventDto.builder()
                .topic(topic)
                .action(action)
                .entityId(entityId)
                .isYouth(isYouth)
                .timestamp(Instant.now().toString())
                .build();

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    broadcast(event);
                }
            });
        } else {
            broadcast(event);
        }
    }

    public void broadcast(RealtimeEventDto event) {
        if (sessions.isEmpty()) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(event);
            TextMessage textMessage = new TextMessage(json);
            for (WebSocketSession session : sessions) {
                if (!session.isOpen()) {
                    sessions.remove(session);
                    continue;
                }
                try {
                    synchronized (session) {
                        session.sendMessage(textMessage);
                    }
                } catch (IOException ex) {
                    sessions.remove(session);
                }
            }
        } catch (Exception ex) {
            log.warn("[WebSocket] Erro ao transmitir evento {}: {}", event.getAction(), ex.getMessage());
        }
    }
}

