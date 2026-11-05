package com.esporteclubepelotas.api.modules.idol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrevNextIdolResponseDto {
    private IdolDto previous;
    private IdolDto next;
}

