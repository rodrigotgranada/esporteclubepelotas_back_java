package com.esporteclubepelotas.api.modules.idol.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetIdolsResponseDto {
    private List<IdolDto> data;
    private long total;
    private int page;
    private int limit;
    private int totalPages;
    private Boolean isModuleActive;

    public static GetIdolsResponseDto of(List<IdolDto> data, long total, int page, int limit, Boolean isModuleActive) {
        int pages = limit > 0 ? (int) Math.ceil((double) total / limit) : 1;
        return GetIdolsResponseDto.builder()
                .data(data)
                .total(total)
                .page(page)
                .limit(limit)
                .totalPages(Math.max(pages, 1))
                .isModuleActive(isModuleActive)
                .build();
    }
}

