package com.esporteclubepelotas.api.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper;

    @Value("${app.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    private static final long ONE_MINUTE_MS = 60000L;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Usa getRemoteAddr() resolvido nativamente pelo container (server.forward-headers-strategy=native),
        // evitando bypass via header X-Forwarded-For forjado pelo cliente.
        String clientIp = request.getRemoteAddr() != null ? request.getRemoteAddr() : "127.0.0.1";
        boolean allowed = true;

        if ("POST".equalsIgnoreCase(method)) {
            if (path.endsWith("/auth/login")) {
                allowed = rateLimiterService.tryAcquire("login:" + clientIp, 10, ONE_MINUTE_MS);
            } else if (path.endsWith("/auth/forgot-password")) {
                allowed = rateLimiterService.tryAcquire("forgot-pass:" + clientIp, 3, ONE_MINUTE_MS);
            } else if (path.endsWith("/auth/resend-code")) {
                allowed = rateLimiterService.tryAcquire("resend-code:" + clientIp, 3, ONE_MINUTE_MS);
            } else if (path.endsWith("/auth/verify")) {
                allowed = rateLimiterService.tryAcquire("verify:" + clientIp, 10, ONE_MINUTE_MS);
            } else if (path.endsWith("/auth/reset-password")) {
                allowed = rateLimiterService.tryAcquire("reset-pass:" + clientIp, 5, ONE_MINUTE_MS);
            } else if (path.endsWith("/auth/register") || path.endsWith("/users/register")) {
                allowed = rateLimiterService.tryAcquire("register:" + clientIp, 5, ONE_MINUTE_MS);
            } else if (path.contains("/news/") && path.endsWith("/comments")) {
                allowed = rateLimiterService.tryAcquire("comment:" + clientIp, 5, ONE_MINUTE_MS);
            } else if (path.contains("/news/comments/") && path.endsWith("/report")) {
                allowed = rateLimiterService.tryAcquire("comment-report:" + clientIp, 5, ONE_MINUTE_MS);
            } else if (path.endsWith("/newsletter/subscribe")) {
                allowed = rateLimiterService.tryAcquire("newsletter:" + clientIp, 5, ONE_MINUTE_MS);
            } else if (path.endsWith("/membership-plans/leads")) {
                allowed = rateLimiterService.tryAcquire("leads:" + clientIp, 5, ONE_MINUTE_MS);
            } else if (path.contains("/polls/") && path.endsWith("/vote")) {
                allowed = rateLimiterService.tryAcquire("poll-vote:" + clientIp, 15, ONE_MINUTE_MS);
            }
        } else if ("GET".equalsIgnoreCase(method) && path.endsWith("/users/check-availability")) {
            allowed = rateLimiterService.tryAcquire("check-avail:" + clientIp, 20, ONE_MINUTE_MS);
        }

        if (!allowed) {
            log.warn("Rate limit excedido para IP {} na rota {} {}", clientIp, method, path);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Retry-After", "60");

            Map<String, Object> errorBody = Map.of(
                    "status", HttpStatus.TOO_MANY_REQUESTS.value(),
                    "error", "Too Many Requests",
                    "message", "Muitas requisições. Por favor, aguarde alguns instantes antes de tentar novamente."
            );
            response.getWriter().write(objectMapper.writeValueAsString(errorBody));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
