package com.esporteclubepelotas.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> data;
    private long total;
    private int page;
    private int limit;
    private int totalPages;

    public static <T> PageResponseDto<T> of(List<T> data, long total, int page, int limit) {
        int totalPages = limit > 0 ? (int) Math.ceil((double) total / limit) : 1;
        return PageResponseDto.<T>builder()
                .data(data != null ? data : Collections.emptyList())
                .total(total)
                .page(page)
                .limit(limit)
                .totalPages(Math.max(totalPages, 1))
                .build();
    }
}

