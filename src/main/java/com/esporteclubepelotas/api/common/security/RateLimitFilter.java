package com.esporteclubepelotas.api.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private static final long ONE_MINUTE_MS = 60000L;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = extractClientIp(request);
        boolean allowed = true;
        int maxAllowed = 60;

        if ("POST".equalsIgnoreCase(method)) {
            if (path.endsWith("/auth/login")) {
                maxAllowed = 10;
                allowed = rateLimiterService.tryAcquire("login:" + clientIp, maxAllowed, ONE_MINUTE_MS);
            } else if (path.endsWith("/auth/forgot-password")) {
                maxAllowed = 3;
                allowed = rateLimiterService.tryAcquire("forgot-pass:" + clientIp, maxAllowed, ONE_MINUTE_MS);
            } else if (path.endsWith("/auth/register")) {
                maxAllowed = 5;
                allowed = rateLimiterService.tryAcquire("register:" + clientIp, maxAllowed, ONE_MINUTE_MS);
            } else if (path.contains("/news/") && path.endsWith("/comments")) {
                maxAllowed = 5;
                allowed = rateLimiterService.tryAcquire("comment:" + clientIp, maxAllowed, ONE_MINUTE_MS);
            }
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

    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "127.0.0.1";
    }
}

