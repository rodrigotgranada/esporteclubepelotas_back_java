package com.esporteclubepelotas.api.modules.membership.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMembershipLeadDto {

    @NotNull(message = "O ID do plano é obrigatório")
    private UUID planId;

    private String userId;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 255, message = "O nome não pode exceder 255 caracteres")
    private String name;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "O telefone/WhatsApp é obrigatório")
    @Size(max = 50, message = "Telefone não pode exceder 50 caracteres")
    private String phone;

    private String cpf;

    private String notes;
}
