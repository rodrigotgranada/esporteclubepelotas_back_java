package com.esporteclubepelotas.api.modules.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestChangeDto {
    @NotBlank(message = "O tipo ('email' ou 'phone') é obrigatório")
    private String type;

    @NotBlank(message = "O novo valor é obrigatório")
    private String newValue;

    @NotBlank(message = "A senha atual é obrigatória")
    private String currentPassword;
}

