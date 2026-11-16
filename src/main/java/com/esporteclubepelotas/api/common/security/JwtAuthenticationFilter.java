package com.esporteclubepelotas.api.common.security;

import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final AuthTokenStateService authTokenStateService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Claims claims = tokenProvider.getClaimsFromToken(jwt);
                String tokenType = claims.get("type", String.class);

                // Impede que um Refresh Token seja utilizado como Bearer Access Token
                if ("REFRESH".equalsIgnoreCase(tokenType)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String userId = claims.getSubject();
                Integer tokenVersion = claims.get("tv", Integer.class);
                AuthTokenStateService.UserTokenState state = authTokenStateService.getUserTokenState(userId);

                boolean isValidUserState = state != null
                        && state.active()
                        && state.status() != UserStatus.BLOCKED
                        && state.status() != UserStatus.INACTIVE
                        && state.status() != UserStatus.PENDING
                        && state.emailVerified()
                        && tokenVersion != null
                        && tokenVersion == state.tokenVersion();

                if (!isValidUserState) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String email = claims.get("email", String.class);
                String name = claims.get("name", String.class);
                String cpf = claims.get("cpf", String.class);
                String role = claims.get("role", String.class);
                @SuppressWarnings("unchecked")
                List<String> permissions = (List<String>) claims.get("permissions");

                UserPrincipal principal = UserPrincipal.builder()
                        .id(userId)
                        .email(email)
                        .name(name)
                        .cpf(cpf)
                        .role(role)
                        .permissions(permissions)
                        .tokenVersion(tokenVersion)
                        .active(true)
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Não foi possível autenticar o usuário através do token JWT", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
