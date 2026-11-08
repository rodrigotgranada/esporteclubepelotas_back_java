package com.esporteclubepelotas.api.modules.sponsor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetSponsorsResponseDto {

    private List<SponsorDto> data;
    private long total;
    private int page;
    private int limit;
    private int totalPages;
}

