package com.esporteclubepelotas.api.modules.integrations.store.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.UnauthorizedException;
import com.esporteclubepelotas.api.modules.integrations.store.dto.StoreOrderBridgeDto.StoreSsoTokenResponseDto;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class StoreSsoTokenService {

    private static final int SSO_TTL_SECONDS = 60;

    private final UserRepository userRepository;

    @Value("${app.store.internal-api-key:}")
    private String storeInternalApiKey;

    @Transactional(readOnly = true)
    public StoreSsoTokenResponseDto issueSsoToken(String userId) {
        if (storeInternalApiKey == null || storeInternalApiKey.isBlank()) {
            throw new BusinessException("Integração SSO com a Loja Oficial ainda não configurada.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado."));

        if (!user.isActive() || user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("Conta inativa ou aguardando verificação de e-mail.");
        }

        String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " "
                + (user.getLastName() != null ? user.getLastName() : "")).trim();
        String roleName = user.getRole() != null && user.getRole().getName() != null
                ? user.getRole().getName()
                : "USER";
        boolean isSocioActive = user.isSocio() && "ACTIVE".equalsIgnoreCase(user.getSocioStatus());

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + (SSO_TTL_SECONDS * 1000L));

        String token = Jwts.builder()
                .subject(user.getId())
                .claim("type", "STORE_SSO")
                .claim("fullName", fullName)
                .claim("email", user.getEmail())
                .claim("cpf", user.getCpf())
                .claim("role", roleName)
                .claim("isSocioActive", isSocioActive)
                .claim("socioPlanName", user.getSocioPlanName())
                .claim("socioStatus", user.getSocioStatus())
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(deriveSigningKey())
                .compact();

        return StoreSsoTokenResponseDto.builder()
                .ssoToken(token)
                .expiresInSeconds(SSO_TTL_SECONDS)
                .build();
    }

    private SecretKey deriveSigningKey() {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(storeInternalApiKey.trim().getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível", e);
        }
    }
}
