package com.esporteclubepelotas.api.common.security;

import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mantém estado leve e cacheado de versão de token e status ativo do usuário
 * para permitir revogação imediata de Access Tokens após troca de senha, bloqueio ou exclusão.
 */
@Service
@RequiredArgsConstructor
public class AuthTokenStateService {

    public record UserTokenState(int tokenVersion, boolean active, UserStatus status, boolean emailVerified) {}

    private final UserRepository userRepository;

    @Cacheable(value = "user_token_state", key = "#userId")
    @Transactional(readOnly = true)
    public UserTokenState getUserTokenState(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }
        return userRepository.findById(userId)
                .map(this::toState)
                .orElse(null);
    }

    @CacheEvict(value = "user_token_state", key = "#userId")
    public void evictUserTokenState(String userId) {
        // Eviction controlada por anotação Spring Cache
    }

    private UserTokenState toState(User user) {
        return new UserTokenState(
                user.getTokenVersion(),
                user.isActive(),
                user.getStatus(),
                user.isEmailVerified()
        );
    }
}

