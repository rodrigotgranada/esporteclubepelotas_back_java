package com.esporteclubepelotas.api.modules.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrevNextNewsResponseDto {

    private PrevNextNewsDto previous;
    private PrevNextNewsDto next;
}

