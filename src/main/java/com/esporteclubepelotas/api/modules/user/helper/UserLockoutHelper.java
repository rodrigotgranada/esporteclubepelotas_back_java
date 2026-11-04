package com.esporteclubepelotas.api.modules.user.helper;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class UserLockoutHelper {

    private UserLockoutHelper() {}

    public static void checkLockout(User user) {
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new BusinessException("Conta bloqueada por múltiplas tentativas falhas. Entre em contato com o suporte.");
        }
        if (user.getLockoutUntil() != null && user.getLockoutUntil().isAfter(Instant.now())) {
            throw new BusinessException("Muitas tentativas falhas. Conta temporariamente bloqueada. Tente novamente em 10 minutos.");
        }
    }

    public static void handleFailedLoginAttempt(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

        if (user.getFailedLoginAttempts() >= 10) {
            user.setStatus(UserStatus.BLOCKED);
            user.setLockoutUntil(null);
        } else if (user.getFailedLoginAttempts() >= 5) {
            user.setLockoutUntil(Instant.now().plus(10, ChronoUnit.MINUTES));
        }
    }

    public static void resetLoginAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0 || user.getLockoutUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setLockoutUntil(null);
        }
    }
}

