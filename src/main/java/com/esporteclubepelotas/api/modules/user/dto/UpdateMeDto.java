package com.esporteclubepelotas.api.modules.user.dto;

import com.esporteclubepelotas.api.modules.user.entity.AddressItem;
import com.esporteclubepelotas.api.modules.user.entity.PhoneItem;
import com.esporteclubepelotas.api.modules.user.entity.UserPreferences;
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
public class UpdateMeDto {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String avatarUrl;
    private List<AddressItem> addresses;
    private List<PhoneItem> phones;
    private UserPreferences preferences;
}

