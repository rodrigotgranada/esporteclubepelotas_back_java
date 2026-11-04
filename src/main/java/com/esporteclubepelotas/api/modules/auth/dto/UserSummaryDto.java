package com.esporteclubepelotas.api.modules.auth.dto;

import com.esporteclubepelotas.api.modules.user.entity.AddressItem;
import com.esporteclubepelotas.api.modules.user.entity.PendingChangeItem;
import com.esporteclubepelotas.api.modules.user.entity.PhoneItem;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserPreferences;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSummaryDto {
    private String id;

    @JsonProperty("_id")
    public String getMongoId() {
        return id;
    }

    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;
    private String cpf;
    private LocalDate birthDate;
    private String phone;
    private String avatarUrl;
    private RoleSummaryDto role;
    private UserStatus status;
    private boolean mustChangePassword;
    private boolean isActive;

    private List<PhoneItem> phones;
    private List<AddressItem> addresses;
    private UserPreferences preferences;
    private List<PendingChangeItem> pendingChanges;

    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleSummaryDto {
        private String id;
        @JsonProperty("_id")
        public String getMongoId() {
            return id;
        }
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
                .emailVerified(user.isEmailVerified())
                .cpf(user.getCpf())
                .birthDate(user.getBirthDate())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(roleDto)
                .status(user.getStatus())
                .mustChangePassword(user.isMustChangePassword())
                .isActive(user.isActive())
                .phones(user.getPhones())
                .addresses(user.getAddresses())
                .preferences(user.getPreferences())
                .pendingChanges(user.getPendingChanges())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
