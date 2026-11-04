package com.esporteclubepelotas.api.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "chave-secreta-de-teste-do-esporte-clube-pelotas-1908");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiresIn", "1h");
        jwtTokenProvider.init();
    }

    @Test
    @DisplayName("Deve gerar e validar Access Token com sucesso contendo claims")
    void shouldGenerateAndValidateAccessToken() {
        UserPrincipal principal = UserPrincipal.builder()
                .id("user-123")
                .name("Torcedor Lobo")
                .email("torcedor@pelotas.com.br")
                .cpf("000.000.000-00")
                .role("ADMIN")
                .permissions(List.of("news.manage", "football.manage"))
                .active(true)
                .build();

        String token = jwtTokenProvider.generateAccessToken(principal);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("user-123", jwtTokenProvider.getUserIdFromToken(token));

        var claims = jwtTokenProvider.getClaimsFromToken(token);
        assertEquals("Torcedor Lobo", claims.get("name"));
        assertEquals("torcedor@pelotas.com.br", claims.get("email"));
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    @DisplayName("Deve retornar falso para token inválido ou corrompido")
    void shouldReturnFalseForInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("token-invalido-qualquer"));
        assertFalse(jwtTokenProvider.validateToken(""));
        assertFalse(jwtTokenProvider.validateToken(null));
    }

    @Test
    @DisplayName("Deve gerar Refresh Token com sucesso")
    void shouldGenerateRefreshToken() {
        UserPrincipal principal = UserPrincipal.builder()
                .id("user-456")
                .email("socio@pelotas.com.br")
                .active(true)
                .build();

        String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

        assertNotNull(refreshToken);
        assertTrue(jwtTokenProvider.validateToken(refreshToken));
        assertEquals("user-456", jwtTokenProvider.getUserIdFromToken(refreshToken));
    }
}
