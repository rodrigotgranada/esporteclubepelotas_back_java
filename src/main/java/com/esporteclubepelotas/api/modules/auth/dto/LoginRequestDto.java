package com.esporteclubepelotas.api.modules.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    // Aceita tanto "cpf" quanto "identifier" ou "email"
    @NotBlank(message = "O CPF ou E-mail é obrigatório")
    @JsonAlias({"identifier", "email"})
    private String cpf;

    @NotBlank(message = "A senha é obrigatória")
    private String password;
}

