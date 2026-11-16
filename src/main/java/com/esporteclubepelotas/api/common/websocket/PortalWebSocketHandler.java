package com.esporteclubepelotas.api.common.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class PortalWebSocketHandler extends TextWebSocketHandler {

    private static final int MAX_TOTAL_SESSIONS = 500;
    private static final int MAX_SESSIONS_PER_IP = 10;
    private static final int SEND_TIME_LIMIT_MS = 2000;
    private static final int BUFFER_SIZE_LIMIT_BYTES = 16 * 1024;

    private final ObjectMapper objectMapper;
    private final Executor taskExecutor;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> sessionsPerIp = new ConcurrentHashMap<>();

    public PortalWebSocketHandler(
            ObjectMapper objectMapper,
            @Qualifier("taskExecutor") Executor taskExecutor
    ) {
        this.objectMapper = objectMapper;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        if (sessions.size() >= MAX_TOTAL_SESSIONS) {
            log.warn("[WebSocket] Limite global de conexões ({}) atingido. Recusando sessão {}", MAX_TOTAL_SESSIONS, session.getId());
            closeQuietly(session, CloseStatus.SERVICE_OVERLOAD);
            return;
        }

        String clientIp = extractClientIp(session);
        AtomicInteger counter = sessionsPerIp.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
        if (counter.incrementAndGet() > MAX_SESSIONS_PER_IP) {
            counter.decrementAndGet();
            log.warn("[WebSocket] Limite de conexões por IP ({}) excedido para {}", MAX_SESSIONS_PER_IP, clientIp);
            closeQuietly(session, CloseStatus.POLICY_VIOLATION);
            return;
        }

        WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(
                session, SEND_TIME_LIMIT_MS, BUFFER_SIZE_LIMIT_BYTES
        );
        sessions.put(session.getId(), safeSession);
        log.debug("[WebSocket] Sessão conectada: {} (ativas: {})", session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if ("PING".equalsIgnoreCase(payload != null ? payload.trim() : "")) {
            WebSocketSession safeSession = sessions.getOrDefault(session.getId(), session);
            try {
                if (safeSession.isOpen()) {
                    safeSession.sendMessage(new TextMessage("PONG"));
                }
            } catch (Exception ignored) {
                removeSession(session);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        removeSession(session);
        log.debug("[WebSocket] Sessão encerrada: {} (ativas: {})", session.getId(), sessions.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        removeSession(session);
        closeQuietly(session, CloseStatus.SERVER_ERROR);
    }

    public void broadcastAfterCommit(String topic, String action, String entityId) {
        broadcastAfterCommit(topic, action, entityId, null);
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
                    taskExecutor.execute(() -> broadcast(event));
                }
            });
        } else {
            taskExecutor.execute(() -> broadcast(event));
        }
    }

    public void broadcast(RealtimeEventDto event) {
        if (sessions.isEmpty()) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(event);
            TextMessage textMessage = new TextMessage(json);
            for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
                WebSocketSession session = entry.getValue();
                if (!session.isOpen()) {
                    removeSession(session);
                    continue;
                }
                try {
                    session.sendMessage(textMessage);
                } catch (Exception ex) {
                    removeSession(session);
                }
            }
        } catch (Exception ex) {
            log.warn("[WebSocket] Erro ao transmitir evento {}: {}", event.getAction(), ex.getMessage());
        }
    }

    private void removeSession(WebSocketSession session) {
        WebSocketSession removed = sessions.remove(session.getId());
        if (removed != null) {
            String ip = extractClientIp(session);
            AtomicInteger counter = sessionsPerIp.get(ip);
            if (counter != null && counter.decrementAndGet() <= 0) {
                sessionsPerIp.remove(ip, counter);
            }
        }
    }

    private String extractClientIp(WebSocketSession session) {
        InetSocketAddress remote = session.getRemoteAddress();
        if (remote != null && remote.getAddress() != null) {
            return remote.getAddress().getHostAddress();
        }
        return "unknown";
    }

    private void closeQuietly(WebSocketSession session, CloseStatus status) {
        try {
            if (session.isOpen()) {
                session.close(status);
            }
        } catch (IOException ignored) {
        }
    }
}
