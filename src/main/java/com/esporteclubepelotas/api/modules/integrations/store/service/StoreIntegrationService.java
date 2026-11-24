package com.esporteclubepelotas.api.modules.integrations.store.service;

import com.esporteclubepelotas.api.common.exception.UnauthorizedException;
import com.esporteclubepelotas.api.modules.auth.dto.AuthResponseDto;
import com.esporteclubepelotas.api.modules.auth.dto.LoginRequestDto;
import com.esporteclubepelotas.api.modules.auth.dto.UserSummaryDto;
import com.esporteclubepelotas.api.modules.auth.service.AuthService;
import com.esporteclubepelotas.api.modules.integrations.store.dto.StoreMemberStatusDto;
import com.esporteclubepelotas.api.modules.integrations.store.dto.StorePortalUserDto;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class StoreIntegrationService {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Value("${app.store.internal-api-key:}")
    private String storeInternalApiKey;

    public void validateInternalKey(String providedKey) {
        if (storeInternalApiKey == null || storeInternalApiKey.isBlank()
                || providedKey == null || providedKey.isBlank()) {
            throw new UnauthorizedException("Chave de integração S2S inválida ou não configurada.");
        }
        boolean match = MessageDigest.isEqual(
                storeInternalApiKey.trim().getBytes(StandardCharsets.UTF_8),
                providedKey.trim().getBytes(StandardCharsets.UTF_8)
        );
        if (!match) {
            throw new UnauthorizedException("Chave de integração S2S inválida.");
        }
    }

    @Transactional
    public StorePortalUserDto authenticateForStore(String apiKey, LoginRequestDto request) {
        validateInternalKey(apiKey);
        AuthResponseDto auth = authService.login(request);
        if (auth.getAccessToken() == null || auth.getAccessToken().isBlank() || auth.getUser() == null) {
            throw new UnauthorizedException("Confirme seu e-mail no Portal antes de acessar a Loja.");
        }

        UserSummaryDto user = auth.getUser();
        String roleName = user.getRole() != null && user.getRole().getName() != null
                ? user.getRole().getName()
                : "USER";
        boolean isSocioActive = user.isSocio() && "ACTIVE".equalsIgnoreCase(user.getSocioStatus());
        String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " "
                + (user.getLastName() != null ? user.getLastName() : "")).trim();

        return StorePortalUserDto.builder()
                .portalUserId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(fullName)
                .email(user.getEmail())
                .cpf(user.getCpf())
                .role(roleName)
                .isSocioActive(isSocioActive)
                .socioPlanName(user.getSocioPlanName())
                .socioStatus(user.getSocioStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public StoreMemberStatusDto findMemberByCpf(String apiKey, String rawCpf) {
        validateInternalKey(apiKey);
        String cleanCpf = rawCpf != null ? rawCpf.replaceAll("[^0-9]", "") : "";
        if (cleanCpf.length() != 11) {
            return StoreMemberStatusDto.builder()
                    .found(false)
                    .isSocioActive(false)
                    .build();
        }

        return userRepository.findByCpf(cleanCpf)
                .filter(user -> user.isActive() && user.getStatus() == UserStatus.ACTIVE)
                .map(this::toMemberStatusDto)
                .orElse(StoreMemberStatusDto.builder()
                        .found(false)
                        .isSocioActive(false)
                        .build());
    }

    private StoreMemberStatusDto toMemberStatusDto(User user) {
        boolean isSocioActive = user.isSocio() && "ACTIVE".equalsIgnoreCase(user.getSocioStatus());
        String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " "
                + (user.getLastName() != null ? user.getLastName() : "")).trim();

        return StoreMemberStatusDto.builder()
                .found(true)
                .portalUserId(user.getId())
                .name(fullName)
                .isSocioActive(isSocioActive)
                .socioPlanName(user.getSocioPlanName())
                .socioStatus(user.getSocioStatus())
                .build();
    }
}
