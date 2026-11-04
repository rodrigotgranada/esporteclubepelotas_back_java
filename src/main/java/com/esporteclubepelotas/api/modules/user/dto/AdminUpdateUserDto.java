package com.esporteclubepelotas.api.modules.user.dto;

import com.esporteclubepelotas.api.modules.user.entity.AddressItem;
import com.esporteclubepelotas.api.modules.user.entity.PhoneItem;
import com.esporteclubepelotas.api.modules.user.entity.UserPreferences;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminUpdateUserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String cpf;
    private LocalDate birthDate;
    private String phone;
    private String role;
    private UserStatus status;
    private String avatarUrl;
    private String password;
    private Boolean mustChangePassword;

    private List<PhoneItem> phones;
    private List<AddressItem> addresses;
    private UserPreferences preferences;
}

