package com.esporteclubepelotas.api.modules.user.service;

import com.esporteclubepelotas.api.common.dto.PageResponseDto;
import com.esporteclubepelotas.api.common.email.EmailService;
import com.esporteclubepelotas.api.common.email.EmailTemplateHelper;
import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.modules.auth.dto.UserSummaryDto;
import com.esporteclubepelotas.api.modules.role.entity.Role;
import com.esporteclubepelotas.api.modules.role.repository.RoleRepository;
import com.esporteclubepelotas.api.modules.user.dto.AdminUpdateUserDto;
import com.esporteclubepelotas.api.modules.user.dto.CheckAvailabilityResponseDto;
import com.esporteclubepelotas.api.modules.user.dto.CreateUserDto;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.esporteclubepelotas.api.modules.user.helper.UserHierarchyHelper;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;
    private final EmailService emailService;

    @Value("${frontend.url:https://esporte-clube-pelotas-dev.vercel.app}")
    private String frontendUrl;

    @Transactional(readOnly = true)
    public PageResponseDto<UserSummaryDto> listUsers(int page, int limit, String search, String role, String status) {
        int pageIndex = Math.max(page - 1, 0);
        int pageSize = limit > 0 ? limit : 15;
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        String searchClean = (search != null && !search.isBlank()) ? search.trim().toLowerCase() : null;
        String roleClean = (role != null && !role.isBlank()) ? UserHierarchyHelper.normalizeRoleName(role.trim()).toUpperCase() : null;

        UserStatus userStatus = null;
        boolean includeInactive = false;

        if (status != null && !status.isBlank()) {
            if ("ALL".equalsIgnoreCase(status.trim())) {
                includeInactive = true;
            } else {
                try {
                    userStatus = UserStatus.valueOf(status.trim().toUpperCase());
                    includeInactive = (userStatus == UserStatus.INACTIVE);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        final boolean targetIncludeInactive = includeInactive;
        final UserStatus targetStatus = userStatus;
        final String targetRole = roleClean;
        final String targetSearch = searchClean;

        Specification<User> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!targetIncludeInactive) {
                predicates.add(cb.isTrue(root.get("isActive")));
            }
            if (targetStatus != null) {
                predicates.add(cb.equal(root.get("status"), targetStatus));
            }
            if (targetRole != null) {
                predicates.add(cb.equal(root.get("role").get("name"), targetRole));
            }
            if (targetSearch != null) {
                String pattern = "%" + targetSearch + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(root.get("cpf"), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> usersPage = userRepository.findAll(spec, pageable);

        List<UserSummaryDto> dtos = usersPage.getContent().stream()
                .map(UserSummaryDto::fromEntity)
                .toList();

        return PageResponseDto.of(dtos, usersPage.getTotalElements(), page, limit);
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        return UserSummaryDto.fromEntity(user);
    }

    @Transactional
    public UserSummaryDto adminCreate(CreateUserDto dto, MultipartFile avatar) {
        String cleanEmail = dto.getEmail().trim().toLowerCase();
        String cleanCpf = dto.getCpf().replaceAll("[^0-9]", "");

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new BusinessException("E-mail já está em uso por outro usuário.");
        }
        if (userRepository.existsByCpf(cleanCpf)) {
            throw new BusinessException("CPF já está em uso por outro usuário.");
        }

        String targetRoleName = (dto.getRole() != null && !dto.getRole().isBlank())
                ? UserHierarchyHelper.normalizeRoleName(dto.getRole())
                : "USER";

        Role role = roleRepository.findByName(targetRoleName)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", "name", targetRoleName));

        User user = User.builder()
                .firstName(dto.getFirstName().trim())
                .lastName(dto.getLastName().trim())
                .email(cleanEmail)
                .cpf(cleanCpf)
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .birthDate(dto.getBirthDate())
                .phone(dto.getPhone())
                .role(role)
                .status(UserStatus.ACTIVE)
                .isActive(true)
                .emailVerified(true)
                .mustChangePassword(Boolean.TRUE.equals(dto.getMustChangePassword()))
                .phones(dto.getPhones())
                .addresses(dto.getAddresses())
                .preferences(dto.getPreferences())
                .build();

        User savedUser = userRepository.save(user);

        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = storageService.uploadFile(avatar, "users/" + savedUser.getId(), "_profile.jpg");
            savedUser.setAvatarUrl(avatarUrl);
            savedUser = userRepository.save(savedUser);
        }

        return UserSummaryDto.fromEntity(savedUser);
    }

    @Transactional
    public UserSummaryDto adminUpdate(String id, AdminUpdateUserDto dto, String actorRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));

        String targetRoleName = user.getRole() != null ? user.getRole().getName() : "USER";
        boolean isBlocking = (dto.getStatus() == UserStatus.BLOCKED || dto.getStatus() == UserStatus.INACTIVE);
        UserHierarchyHelper.checkHierarchy(actorRole, targetRoleName, isBlocking ? "delete" : "edit");

        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            String newRoleName = UserHierarchyHelper.normalizeRoleName(dto.getRole());
            if (!newRoleName.equalsIgnoreCase(targetRoleName)) {
                UserHierarchyHelper.checkHierarchy(actorRole, newRoleName, "promote");
                Role newRole = roleRepository.findByName(newRoleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Cargo", "name", newRoleName));
                user.setRole(newRole);
            }
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String newEmail = dto.getEmail().trim().toLowerCase();
            if (!newEmail.equalsIgnoreCase(user.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new BusinessException("E-mail já está em uso por outro usuário.");
                }
                user.setEmail(newEmail);
            }
        }

        if (dto.getCpf() != null && !dto.getCpf().isBlank()) {
            String newCpf = dto.getCpf().replaceAll("[^0-9]", "");
            if (!newCpf.equals(user.getCpf())) {
                if (userRepository.existsByCpf(newCpf)) {
                    throw new BusinessException("CPF já está em uso por outro usuário.");
                }
                user.setCpf(newCpf);
            }
        }

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName().trim());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName().trim());
        if (dto.getBirthDate() != null) user.setBirthDate(dto.getBirthDate());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone().trim());
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
            if (dto.getStatus() == UserStatus.ACTIVE) {
                user.setFailedLoginAttempts(0);
                user.setLockoutUntil(null);
            }
        }
        if (dto.getAvatarUrl() != null) user.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getPhones() != null) user.setPhones(dto.getPhones());
        if (dto.getAddresses() != null) user.setAddresses(dto.getAddresses());
        if (dto.getPreferences() != null) user.setPreferences(dto.getPreferences());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            user.setMustChangePassword(Boolean.TRUE.equals(dto.getMustChangePassword()));
            user.setTokenVersion(user.getTokenVersion() + 1);
        } else if (dto.getMustChangePassword() != null) {
            user.setMustChangePassword(dto.getMustChangePassword());
        }

        return UserSummaryDto.fromEntity(userRepository.save(user));
    }

    @Transactional
    public Map<String, String> adminUpdateAvatar(String id, MultipartFile file, String actorRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));

        String targetRole = user.getRole() != null ? user.getRole().getName() : "USER";
        UserHierarchyHelper.checkHierarchy(actorRole, targetRole, "edit");

        String avatarUrl = storageService.uploadFile(file, "users/" + user.getId(), "_profile.jpg");
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);

        return Map.of("url", avatarUrl);
    }

    @Transactional
    public UserSummaryDto adminDeleteAvatar(String id, String actorRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));

        String targetRole = user.getRole() != null ? user.getRole().getName() : "USER";
        UserHierarchyHelper.checkHierarchy(actorRole, targetRole, "edit");

        user.setAvatarUrl(null);
        return UserSummaryDto.fromEntity(userRepository.save(user));
    }

    @Transactional
    public UserSummaryDto updateUserRole(String userId, String roleName, String actorRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        String targetRole = user.getRole() != null ? user.getRole().getName() : "USER";
        UserHierarchyHelper.checkHierarchy(actorRole, targetRole, "edit");

        String normalizedNewRole = UserHierarchyHelper.normalizeRoleName(roleName);
        UserHierarchyHelper.checkHierarchy(actorRole, normalizedNewRole, "promote");

        Role role = roleRepository.findByName(normalizedNewRole)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", "name", normalizedNewRole));

        user.setRole(role);
        return UserSummaryDto.fromEntity(userRepository.save(user));
    }

    @Transactional
    public UserSummaryDto updateUserStatus(String userId, String statusStr, String actorRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        try {
            UserStatus status = UserStatus.valueOf(statusStr.toUpperCase());
            String targetRole = user.getRole() != null ? user.getRole().getName() : "USER";
            boolean isBlocking = (status == UserStatus.BLOCKED || status == UserStatus.INACTIVE);
            UserHierarchyHelper.checkHierarchy(actorRole, targetRole, isBlocking ? "delete" : "edit");

            user.setStatus(status);
            user.setActive(status != UserStatus.INACTIVE);
            if (status == UserStatus.ACTIVE) {
                user.setFailedLoginAttempts(0);
                user.setLockoutUntil(null);
            }
            return UserSummaryDto.fromEntity(userRepository.save(user));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Status inválido: " + statusStr);
        }
    }

    @Transactional
    public void adminSoftDelete(String userId, String actorRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        String targetRole = user.getRole() != null ? user.getRole().getName() : "USER";
        UserHierarchyHelper.checkHierarchy(actorRole, targetRole, "delete");

        user.setActive(false);
        user.setStatus(UserStatus.INACTIVE);
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    @Transactional
    public void adminResendVerification(String userId, String actorRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        String targetRole = user.getRole() != null ? user.getRole().getName() : "USER";
        UserHierarchyHelper.checkHierarchy(actorRole, targetRole, "edit");

        if (user.getStatus() == UserStatus.ACTIVE && user.isEmailVerified()) {
            throw new BusinessException("Usuário já está com e-mail verificado e ativo.");
        }

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        user.setVerificationCode(code);
        user.setVerificationCodeExpires(Instant.now().plus(60, ChronoUnit.MINUTES));
        userRepository.save(user);

        String emailHtml = EmailTemplateHelper.getVerificationEmail(code, true);
        emailService.sendHtmlEmail(user.getEmail(), "Novo Código de Verificação - E.C. Pelotas", emailHtml);
    }

    @Transactional
    public void adminForcePasswordReset(String userId, String actorRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        String targetRole = user.getRole() != null ? user.getRole().getName() : "USER";
        UserHierarchyHelper.checkHierarchy(actorRole, targetRole, "edit");

        String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        user.setResetPasswordToken(token);
        user.setResetPasswordExpires(Instant.now().plus(60, ChronoUnit.MINUTES));
        userRepository.save(user);

        String emailHtml = EmailTemplateHelper.getResetPasswordEmail(frontendUrl, token);
        emailService.sendHtmlEmail(user.getEmail(), "Redefinição de Senha Solicitada pelo Administrador - E.C. Pelotas", emailHtml);
    }

    @Transactional(readOnly = true)
    public CheckAvailabilityResponseDto checkAvailability(String email, String cpf) {
        if (email != null && !email.isBlank()) {
            boolean inUse = userRepository.existsByEmail(email.trim().toLowerCase());
            return CheckAvailabilityResponseDto.builder()
                    .available(!inUse)
                    .message(inUse ? "E-mail já está em uso" : "E-mail disponível")
                    .build();
        }
        if (cpf != null && !cpf.isBlank()) {
            String cleanCpf = cpf.replaceAll("[^0-9]", "");
            boolean inUse = userRepository.existsByCpf(cleanCpf);
            return CheckAvailabilityResponseDto.builder()
                    .available(!inUse)
                    .message(inUse ? "CPF já está em uso" : "CPF disponível")
                    .build();
        }
        return CheckAvailabilityResponseDto.builder().available(true).message("Informe email ou cpf").build();
    }
}
