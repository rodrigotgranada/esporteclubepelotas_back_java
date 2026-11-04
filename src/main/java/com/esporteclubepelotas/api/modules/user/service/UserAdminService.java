package com.esporteclubepelotas.api.modules.user.service;

import com.esporteclubepelotas.api.common.dto.PageResponseDto;
import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.modules.auth.dto.UserSummaryDto;
import com.esporteclubepelotas.api.modules.role.entity.Role;
import com.esporteclubepelotas.api.modules.role.repository.RoleRepository;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public PageResponseDto<UserSummaryDto> listUsers(int page, int limit, String search) {
        int pageIndex = Math.max(page - 1, 0);
        int pageSize = limit > 0 ? limit : 15;
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> usersPage = (search != null && !search.isBlank())
                ? userRepository.searchUsers(search.trim(), pageable)
                : userRepository.findAllByIsActiveTrue(pageable);

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
    public UserSummaryDto updateUserRole(String userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        Role role = roleRepository.findByName(roleName.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));

        user.setRole(role);
        return UserSummaryDto.fromEntity(userRepository.save(user));
    }

    @Transactional
    public UserSummaryDto updateUserStatus(String userId, String statusStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId));

        try {
            UserStatus status = UserStatus.valueOf(statusStr.toUpperCase());
            user.setStatus(status);
            user.setActive(status != UserStatus.INACTIVE);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Status inválido: " + statusStr);
        }

        return UserSummaryDto.fromEntity(userRepository.save(user));
    }
}
