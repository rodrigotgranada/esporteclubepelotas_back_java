package com.esporteclubepelotas.api.modules.auth.service;

import com.esporteclubepelotas.api.common.email.EmailService;
import com.esporteclubepelotas.api.common.email.EmailTemplateHelper;
import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.UnauthorizedException;
import com.esporteclubepelotas.api.common.security.JwtTokenProvider;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.modules.auth.dto.*;
import com.esporteclubepelotas.api.modules.membership.entity.EleventicketsMemberCache;
import com.esporteclubepelotas.api.modules.membership.repository.EleventicketsMemberCacheRepository;
import com.esporteclubepelotas.api.modules.role.entity.Role;
import com.esporteclubepelotas.api.modules.role.repository.RoleRepository;
import com.esporteclubepelotas.api.modules.user.dto.CreateUserDto;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.esporteclubepelotas.api.modules.user.helper.UserHierarchyHelper;
import com.esporteclubepelotas.api.modules.user.helper.UserLockoutHelper;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EleventicketsMemberCacheRepository cacheRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StorageService storageService;
    private final EmailService emailService;

    @Value("${frontend.url:https://esporte-clube-pelotas-dev.vercel.app}")
    private String frontendUrl;

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        String rawIdentifier = request.getCpf() != null ? request.getCpf().trim() : "";
        String cleanCpf = rawIdentifier.replaceAll("[^0-9]", "");

        User user = null;
        if (!cleanCpf.isBlank()) {
            user = userRepository.findByCpf(cleanCpf).orElse(null);
        }
        if (user == null && !rawIdentifier.isBlank()) {
            user = userRepository.findByEmail(rawIdentifier.toLowerCase()).orElse(null);
        }

        if (user == null) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        UserLockoutHelper.checkLockout(user);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            UserLockoutHelper.handleFailedLoginAttempt(user);
            userRepository.save(user);
            throw new UnauthorizedException("Credenciais inválidas");
        }

        if (!user.isActive()) {
            throw new UnauthorizedException("Conta inativa ou suspensa. Contate a administração.");
        }

        UserLockoutHelper.resetLoginAttempts(user);
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

    @Transactional
    public UserSummaryDto register(CreateUserDto dto, MultipartFile avatar) {
        String cleanEmail = dto.getEmail().trim().toLowerCase();
        String cleanCpf = dto.getCpf().replaceAll("[^0-9]", "");

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new BusinessException("E-mail já está em uso.");
        }
        if (userRepository.existsByCpf(cleanCpf)) {
            throw new BusinessException("CPF já está em uso.");
        }

        String roleName = (dto.getRole() != null && !dto.getRole().isBlank())
                ? UserHierarchyHelper.normalizeRoleName(dto.getRole())
                : "USER";

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.findByName("USER").orElse(null));

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        Instant expiresAt = Instant.now().plus(60, ChronoUnit.MINUTES);

        User user = User.builder()
                .firstName(dto.getFirstName().trim())
                .lastName(dto.getLastName().trim())
                .email(cleanEmail)
                .cpf(cleanCpf)
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .birthDate(dto.getBirthDate())
                .phone(dto.getPhone())
                .role(role)
                .status(UserStatus.PENDING)
                .isActive(true)
                .emailVerified(false)
                .verificationCode(code)
                .verificationCodeExpires(expiresAt)
                .phones(dto.getPhones())
                .addresses(dto.getAddresses())
                .preferences(dto.getPreferences())
                .build();

        // Auto-vínculo de sócio se o CPF constar no cache pré-importado da ElevenTickets
        Optional<EleventicketsMemberCache> cacheOpt = cacheRepository.findByCpf(cleanCpf);
        if (cacheOpt.isPresent()) {
            EleventicketsMemberCache cache = cacheOpt.get();
            if ("ACTIVE".equalsIgnoreCase(cache.getStatus())) {
                user.setSocio(true);
                user.setEleventicketsId(cache.getEleventicketsId());
                user.setSocioPlanName(cache.getPlanName());
                user.setSocioStatus("ACTIVE");
                user.setSocioSince(LocalDate.now());
                user.setSocioLastSyncedAt(Instant.now());
                Role socioRole = roleRepository.findByName("SOCIO").orElse(null);
                if (socioRole != null && "USER".equals(role.getName())) {
                    user.setRole(socioRole);
                }
                log.info("🎉 Auto-vínculo de Sócio ElevenTickets realizado para novo cadastro: CPF {}", cleanCpf);
            }
        }

        User savedUser = userRepository.save(user);

        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = storageService.uploadFile(avatar, "users/" + savedUser.getId(), "_profile.jpg");
            savedUser.setAvatarUrl(avatarUrl);
            savedUser = userRepository.save(savedUser);
        }

        String emailHtml = EmailTemplateHelper.getVerificationEmail(code, false);
        emailService.sendHtmlEmail(savedUser.getEmail(), "Seu código de verificação - E.C. Pelotas", emailHtml);

        return UserSummaryDto.fromEntity(savedUser);
    }

    @Transactional
    public AuthResponseDto verifyEmail(VerifyEmailRequestDto dto) {
        String cleanEmail = dto.getEmail().trim().toLowerCase();
        String cleanCode = dto.getCode().trim();

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (user.getStatus() == UserStatus.ACTIVE && user.isEmailVerified()) {
            throw new BusinessException("Usuário já está com e-mail verificado");
        }

        UserLockoutHelper.checkLockout(user);

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(cleanCode)) {
            UserLockoutHelper.handleFailedLoginAttempt(user);
            userRepository.save(user);
            throw new BusinessException("Código de confirmação inválido");
        }

        if (user.getVerificationCodeExpires() != null && Instant.now().isAfter(user.getVerificationCodeExpires())) {
            throw new BusinessException("Código de confirmação expirado. Solicite um novo código.");
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpires(null);
        UserLockoutHelper.resetLoginAttempts(user);
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

    @Transactional
    public void resendCode(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException("O e-mail é obrigatório");
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (user.getStatus() == UserStatus.ACTIVE && user.isEmailVerified()) {
            throw new BusinessException("Usuário já está verificado");
        }

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        user.setVerificationCode(code);
        user.setVerificationCodeExpires(Instant.now().plus(60, ChronoUnit.MINUTES));
        userRepository.save(user);

        String emailHtml = EmailTemplateHelper.getVerificationEmail(code, true);
        emailService.sendHtmlEmail(user.getEmail(), "Novo código de verificação - E.C. Pelotas", emailHtml);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto dto) {
        String identifier = dto.getEmail().trim();
        String cleanCpf = identifier.replaceAll("[^0-9]", "");

        User user = null;
        if (!cleanCpf.isBlank()) {
            user = userRepository.findByCpf(cleanCpf).orElse(null);
        }
        if (user == null) {
            user = userRepository.findByEmail(identifier.toLowerCase()).orElse(null);
        }

        if (user == null) {
            throw new BusinessException("Usuário não encontrado");
        }

        String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        user.setResetPasswordToken(token);
        user.setResetPasswordExpires(Instant.now().plus(60, ChronoUnit.MINUTES));
        userRepository.save(user);

        String emailHtml = EmailTemplateHelper.getResetPasswordEmail(frontendUrl, token);
        emailService.sendHtmlEmail(user.getEmail(), "Redefinição de Senha - E.C. Pelotas", emailHtml);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDto dto) {
        User user = userRepository.findByResetPasswordToken(dto.getToken().trim())
                .orElseThrow(() -> new BusinessException("Token de redefinição inválido ou expirado"));

        if (user.getResetPasswordExpires() != null && Instant.now().isAfter(user.getResetPasswordExpires())) {
            throw new BusinessException("Token de redefinição expirado");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpires(null);
        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
        user.setTokenVersion(user.getTokenVersion() + 1);

        if (user.getStatus() == UserStatus.BLOCKED) {
            user.setStatus(UserStatus.ACTIVE);
        }

        userRepository.save(user);
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
            throw new BusinessException("Senha atual incorreta");
        }

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        user.setMustChangePassword(false);
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        return UserSummaryDto.fromEntity(user);
    }

    private UserPrincipal toPrincipal(User user) {
        String roleName = user.getRole() != null ? user.getRole().getName() : "USER";
        return UserPrincipal.builder()
                .id(user.getId())
                .name(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .cpf(user.getCpf())
                .role(roleName)
                .permissions(user.getRole() != null ? user.getRole().getPermissions() : Collections.emptyList())
                .active(user.isActive())
                .build();
    }
}
