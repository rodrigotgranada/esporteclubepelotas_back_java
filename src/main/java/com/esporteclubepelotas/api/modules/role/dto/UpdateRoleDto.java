package com.esporteclubepelotas.api.modules.role.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleDto {

    @Size(max = 100, message = "O rótulo deve ter no máximo 100 caracteres")
    private String label;

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    private String description;

    private List<String> permissions;
}

