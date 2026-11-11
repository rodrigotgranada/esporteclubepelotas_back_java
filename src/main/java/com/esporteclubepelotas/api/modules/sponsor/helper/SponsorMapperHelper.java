package com.esporteclubepelotas.api.modules.sponsor.helper;

import com.esporteclubepelotas.api.modules.sponsor.dto.SponsorDto;
import com.esporteclubepelotas.api.modules.sponsor.entity.Sponsor;

import java.time.LocalDate;

public final class SponsorMapperHelper {

    private SponsorMapperHelper() {}

    public static SponsorDto toDto(Sponsor sponsor) {
        if (sponsor == null) return null;

        LocalDate today = LocalDate.now();
        String status = "ACTIVE";

        if (!sponsor.isActive()) {
            status = "INACTIVE";
        } else if (sponsor.getContractStartDate() != null && sponsor.getContractStartDate().isAfter(today)) {
            status = "SCHEDULED";
        } else if (sponsor.getContractEndDate() != null && sponsor.getContractEndDate().isBefore(today)) {
            status = "EXPIRED";
        }

        return SponsorDto.builder()
                .id(sponsor.getId())
                .name(sponsor.getName())
                .logoUrl(sponsor.getLogoUrl())
                .logoDarkUrl(sponsor.getLogoDarkUrl())
                .websiteUrl(sponsor.getWebsiteUrl())
                .isFeatured(sponsor.isFeatured())
                .isActive(sponsor.isActive())
                .showOnMain(sponsor.isShowOnMain())
                .showOnYouth(sponsor.isShowOnYouth())
                .showOnFemale(sponsor.isShowOnFemale())
                .displayOrder(sponsor.getDisplayOrder() != null ? sponsor.getDisplayOrder() : 0)
                .contractStartDate(sponsor.getContractStartDate())
                .contractEndDate(sponsor.getContractEndDate())
                .clicksCount(sponsor.getClicksCount() != null ? sponsor.getClicksCount() : 0L)
                .status(status)
                .createdAt(sponsor.getCreatedAt())
                .updatedAt(sponsor.getUpdatedAt())
                .build();
    }
}

