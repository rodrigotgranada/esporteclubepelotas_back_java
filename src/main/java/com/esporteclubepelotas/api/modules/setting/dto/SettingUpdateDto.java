package com.esporteclubepelotas.api.modules.setting.dto;

import com.esporteclubepelotas.api.modules.setting.model.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingUpdateDto {

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
    private String membershipSystemProvider;
    private String membershipExternalUrl;
    private List<YouthPillarItem> youthPillars;
    private YouthTryoutsConfig youthTryouts;
}

