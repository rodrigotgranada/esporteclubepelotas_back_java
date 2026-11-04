package com.esporteclubepelotas.api.modules.setting.dto;

import com.esporteclubepelotas.api.modules.setting.entity.Setting;
import com.esporteclubepelotas.api.modules.setting.model.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingResponseDto {

    private String id;

    @JsonProperty("_id")
    private String mongoId;

    private String clubLogoUrl;
    private List<String> clubLogoGallery;
    private String clubName;
    private List<SocialLinkItem> socialLinks;
    private List<ContactPhoneItem> contactPhones;
    private List<ContactEmailItem> contactEmails;
    private List<OpeningHourItem> openingHours;
    private List<FooterNavigationItem> footerNavigationLinks;
    private String stadiumName;
    private String stadiumAddress;
    private String googleMapsEmbedUrl;
    private String copyrightText;
    private Instant createdAt;
    private Instant updatedAt;

    public static SettingResponseDto fromEntity(Setting setting) {
        if (setting == null) return null;
        String idStr = setting.getId() != null ? setting.getId().toString() : null;
        return SettingResponseDto.builder()
                .id(idStr)
                .mongoId(idStr)
                .clubLogoUrl(setting.getClubLogoUrl())
                .clubLogoGallery(setting.getClubLogoGallery())
                .clubName(setting.getClubName())
                .socialLinks(setting.getSocialLinks())
                .contactPhones(setting.getContactPhones())
                .contactEmails(setting.getContactEmails())
                .openingHours(setting.getOpeningHours())
                .footerNavigationLinks(setting.getFooterNavigationLinks())
                .stadiumName(setting.getStadiumName())
                .stadiumAddress(setting.getStadiumAddress())
                .googleMapsEmbedUrl(setting.getGoogleMapsEmbedUrl())
                .copyrightText(setting.getCopyrightText())
                .createdAt(setting.getCreatedAt())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }
}

