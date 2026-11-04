package com.esporteclubepelotas.api.modules.auth.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.UnauthorizedException;
import com.esporteclubepelotas.api.common.security.JwtTokenProvider;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.auth.dto.*;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        String rawIdentifier = request.getCpf() != null ? request.getCpf().trim() : "";
        String cleanCpf = rawIdentifier.replaceAll("[^0-9]", "");

        // Tenta buscar por CPF limpo ou por E-mail
        User user = null;
        if (!cleanCpf.isBlank()) {
            user = userRepository.findByCpf(cleanCpf).orElse(null);
        }
        if (user == null) {
            user = userRepository.findByEmail(rawIdentifier.toLowerCase()).orElse(null);
        }

        if (user == null || !user.isActive()) {
            throw new UnauthorizedException("Credenciais inválidas ou conta inativa");
        }

        // Verifica bloqueio temporário
        if (user.getLockoutUntil() != null && user.getLockoutUntil().isAfter(Instant.now())) {
            throw new UnauthorizedException("Conta temporariamente bloqueada por excesso de tentativas. Tente novamente mais tarde.");
        }

        // Validação da senha criptografada com BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new UnauthorizedException("Credenciais inválidas");
        }

        // Sucesso: zera tentativas de falha
        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
        userRepository.save(user);

        UserPrincipal principal = toPrincipal(user);
        String accessToken = jwtTokenProvider.generateAccessToken(principal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserSummaryDto.fromEntity(user))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
        String token = request.getRefreshToken();
        if (!jwtTokenProvider.validateToken(token)) {
            throw new UnauthorizedException("Refresh token inválido ou expirado");
        }

        String userId = jwtTokenProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

        if (!user.isActive()) {
            throw new UnauthorizedException("Conta inativa");
        }

        UserPrincipal principal = toPrincipal(user);
        String accessToken = jwtTokenProvider.generateAccessToken(principal);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(principal);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .user(UserSummaryDto.fromEntity(user))
                .build();
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getMe(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
        return UserSummaryDto.fromEntity(user);
    }

    @Transactional
    public UserSummaryDto changePassword(String userId, ChangePasswordRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException("A senha atual informada está incorreta");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        user.setMustChangePassword(false);
        user = userRepository.save(user);

        return UserSummaryDto.fromEntity(user);
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= 5) {
            user.setLockoutUntil(Instant.now().plus(15, ChronoUnit.MINUTES));
            log.warn("Usuário {} bloqueado temporariamente por 15 minutos.", user.getEmail());
        }
        userRepository.save(user);
    }

    private UserPrincipal toPrincipal(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .name(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .cpf(user.getCpf())
                .role(user.getRole() != null ? user.getRole().getName() : "USER")
                .permissions(user.getRole() != null ? user.getRole().getPermissions() : Collections.emptyList())
                .active(user.isActive())
                .build();
    }
}
