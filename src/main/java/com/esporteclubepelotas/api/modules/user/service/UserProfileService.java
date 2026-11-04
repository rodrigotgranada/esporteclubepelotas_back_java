package com.esporteclubepelotas.api.modules.user.service;

import com.esporteclubepelotas.api.common.email.EmailService;
import com.esporteclubepelotas.api.common.email.EmailTemplateHelper;
import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.modules.auth.dto.UserSummaryDto;
import com.esporteclubepelotas.api.modules.user.dto.UpdateMeDto;
import com.esporteclubepelotas.api.modules.user.entity.PendingChangeItem;
import com.esporteclubepelotas.api.modules.user.entity.PhoneItem;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public UserSummaryDto getMe(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));
        return UserSummaryDto.fromEntity(user);
    }

    @Transactional
    public UserSummaryDto updateMe(String userId, UpdateMeDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            user.setFirstName(dto.getFirstName().trim());
        }
        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            user.setLastName(dto.getLastName().trim());
        }
        if (dto.getBirthDate() != null) {
            user.setBirthDate(dto.getBirthDate());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }

        if (dto.getAddresses() != null) {
            if (dto.getAddresses().isEmpty() && user.getAddresses() != null && !user.getAddresses().isEmpty()) {
                throw new BusinessException("Você não pode excluir todos os seus endereços. É necessário manter pelo menos um endereço cadastrado.");
            }
            user.setAddresses(dto.getAddresses());
        }

        if (dto.getPhones() != null) {
            user.setPhones(dto.getPhones());
            if (!dto.getPhones().isEmpty()) {
                user.setPhone(dto.getPhones().get(0).getNumber());
            }
        }

        if (dto.getPreferences() != null) {
            user.setPreferences(dto.getPreferences());
        }

        return UserSummaryDto.fromEntity(userRepository.save(user));
    }

    @Transactional
    public Map<String, String> updateAvatar(String userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        String url = storageService.uploadFile(file, "users/" + user.getId(), "_profile.jpg");
        user.setAvatarUrl(url);
        userRepository.save(user);

        return Map.of("url", url);
    }

    @Transactional
    public UserSummaryDto deleteAvatar(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        user.setAvatarUrl(null);
        return UserSummaryDto.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void deactivateMe(String userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        if (password == null || password.isBlank()) {
            throw new BusinessException("A senha é obrigatória para desativar a conta.");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException("Senha atual incorreta.");
        }

        user.setActive(false);
        user.setStatus(UserStatus.INACTIVE);
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BusinessException("Senha atual incorreta.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    @Transactional
    public UserSummaryDto requestChange(String userId, String type, String newValue, String currentPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BusinessException("Senha atual incorreta.");
        }

        String cleanType = type.trim().toLowerCase();
        String cleanValue = newValue.trim();

        if ("email".equalsIgnoreCase(cleanType)) {
            cleanValue = cleanValue.toLowerCase();
            if (cleanValue.equalsIgnoreCase(user.getEmail()) && user.isEmailVerified()) {
                throw new BusinessException("O novo e-mail deve ser diferente do e-mail atual.");
            }
            if (userRepository.existsByEmail(cleanValue)) {
                throw new BusinessException("Este e-mail já está em uso por outra conta.");
            }
        } else if ("phone".equalsIgnoreCase(cleanType)) {
            cleanValue = cleanValue.replaceAll("[^0-9]", "");
        } else {
            throw new BusinessException("Tipo inválido. Use 'email' ou 'phone'.");
        }

        List<PendingChangeItem> changes = user.getPendingChanges() != null ? new ArrayList<>(user.getPendingChanges()) : new ArrayList<>();
        changes.removeIf(c -> cleanType.equalsIgnoreCase(c.getType()));

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        Instant expiresAt = Instant.now().plus(2, ChronoUnit.HOURS);

        changes.add(PendingChangeItem.builder()
                .type(cleanType)
                .newValue(cleanValue)
                .code(code)
                .expiresAt(expiresAt)
                .build());

        user.setPendingChanges(changes);
        userRepository.save(user);

        String targetEmail = "email".equalsIgnoreCase(cleanType) ? cleanValue : user.getEmail();
        String emailHtml = EmailTemplateHelper.getChangeContactEmail(cleanType, code, cleanValue);
        emailService.sendHtmlEmail(targetEmail, "Confirmação de Alteração - E.C. Pelotas", emailHtml);

        return UserSummaryDto.fromEntity(user);
    }

    @Transactional
    public UserSummaryDto confirmChange(String userId, String type, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        String cleanType = type.trim().toLowerCase();
        List<PendingChangeItem> changes = user.getPendingChanges() != null ? new ArrayList<>(user.getPendingChanges()) : new ArrayList<>();

        PendingChangeItem matched = changes.stream()
                .filter(c -> cleanType.equalsIgnoreCase(c.getType()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Nenhuma solicitação de alteração de " + cleanType + " pendente."));

        if (Instant.now().isAfter(matched.getExpiresAt())) {
            changes.remove(matched);
            user.setPendingChanges(changes);
            userRepository.save(user);
            throw new BusinessException("Código expirado. Solicite novamente.");
        }

        if (!matched.getCode().equals(code.trim())) {
            throw new BusinessException("Código de confirmação incorreto.");
        }

        if ("email".equalsIgnoreCase(cleanType)) {
            if (userRepository.existsByEmail(matched.getNewValue())) {
                throw new BusinessException("E-mail já está em uso.");
            }
            user.setEmail(matched.getNewValue());
            user.setEmailVerified(true);
        } else if ("phone".equalsIgnoreCase(cleanType)) {
            List<PhoneItem> phones = user.getPhones() != null ? new ArrayList<>(user.getPhones()) : new ArrayList<>();
            if (phones.isEmpty()) {
                phones.add(PhoneItem.builder()
                        .number(matched.getNewValue())
                        .isPrimary(true)
                        .isWhatsapp(false)
                        .isVerified(true)
                        .build());
            } else {
                PhoneItem p = phones.get(0);
                p.setNumber(matched.getNewValue());
                p.setVerified(true);
            }
            user.setPhones(phones);
            user.setPhone(matched.getNewValue());
        }

        changes.remove(matched);
        user.setPendingChanges(changes);

        return UserSummaryDto.fromEntity(userRepository.save(user));
    }
}

