package com.esporteclubepelotas.api.modules.user.dto;

import com.esporteclubepelotas.api.modules.user.entity.AddressItem;
import com.esporteclubepelotas.api.modules.user.entity.PhoneItem;
import com.esporteclubepelotas.api.modules.user.entity.UserPreferences;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class CreateUserDto {

    @NotBlank(message = "O primeiro nome é obrigatório")
    private String firstName;

    @NotBlank(message = "O sobrenome é obrigatório")
    private String lastName;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    @NotBlank(message = "A senha é obrigatória")
    private String password;

    private LocalDate birthDate;
    private String phone;
    private String role;
    private String avatarUrl;
    private Boolean mustChangePassword;

    private List<PhoneItem> phones;
    private List<AddressItem> addresses;
    private UserPreferences preferences;
}
