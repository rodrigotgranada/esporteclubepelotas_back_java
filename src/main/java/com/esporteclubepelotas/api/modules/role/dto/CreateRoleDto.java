package com.esporteclubepelotas.api.modules.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleDto {

    @NotBlank(message = "O identificador do cargo (name) é obrigatório")
    @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres")
    private String name;

    @Size(max = 100, message = "O rótulo deve ter no máximo 100 caracteres")
    private String label;

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String description;

    @Builder.Default
    private List<String> permissions = new ArrayList<>();
}

