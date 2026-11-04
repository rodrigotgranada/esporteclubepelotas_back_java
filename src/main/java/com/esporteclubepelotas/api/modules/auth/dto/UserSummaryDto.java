package com.esporteclubepelotas.api.modules.auth.dto;

import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSummaryDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String cpf;
    private RoleSummaryDto role;
    private UserStatus status;
    private boolean mustChangePassword;
    private String avatarUrl;
    private boolean isActive;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleSummaryDto {
        private String id;
        private String name;
        private String label;
        private List<String> permissions;
    }

    public static UserSummaryDto fromEntity(User user) {
        if (user == null) return null;

        RoleSummaryDto roleDto = null;
        if (user.getRole() != null) {
            roleDto = RoleSummaryDto.builder()
                    .id(user.getRole().getId())
                    .name(user.getRole().getName())
                    .label(user.getRole().getLabel())
                    .permissions(user.getRole().getPermissions())
                    .build();
        }

        return UserSummaryDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .cpf(user.getCpf())
                .role(roleDto)
                .status(user.getStatus())
                .mustChangePassword(user.isMustChangePassword())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.isActive())
                .build();
    }
}
