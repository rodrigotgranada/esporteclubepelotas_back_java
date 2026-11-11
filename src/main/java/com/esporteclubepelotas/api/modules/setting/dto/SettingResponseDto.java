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
    private String membershipSystemProvider;
    private String membershipExternalUrl;
    private List<YouthPillarItem> youthPillars;
    private YouthTryoutsConfig youthTryouts;

    // Loja Oficial Alcateia
    private String storeName;
    private String storeDescription;
    private String storeFacadeUrl;
    private String storeExternalUrl;
    private Boolean storeIsOnlineActive;
    private String storeInstagramUrl;
    private String storeInstagramDescription;
    private String storeWhatsappNumber;
    private String storeWhatsappDescription;
    private String storeWhatsappMessage;
    private String storeOpeningHours;
    private String storeAddress;
    private Boolean storeHasMemberDiscount;
    private String storeMemberDiscountText;
    private Boolean newsletterIsActive;

    private Instant createdAt;
    private Instant updatedAt;

    public static SettingResponseDto fromEntity(Setting setting) {
        if (setting == null) return null;
        String idStr = setting.getId() != null ? setting.getId().toString() : null;
        return SettingResponseDto.builder()
                .id(idStr)
                .mongoId(idStr)
                .clubLogoUrl(setting.getClubLogoUrl() != null && !setting.getClubLogoUrl().isBlank()
                        ? setting.getClubLogoUrl()
                        : "/images/pelotas-logo.png")
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
                .membershipSystemProvider(setting.getMembershipSystemProvider() != null ? setting.getMembershipSystemProvider() : "ELEVENTICKETS")
                .membershipExternalUrl(setting.getMembershipExternalUrl() != null ? setting.getMembershipExternalUrl() : "https://socioecpelotas.eleventickets.com/#/")
                .youthPillars(setting.getYouthPillars())
                .youthTryouts(setting.getYouthTryouts())
                .storeName(setting.getStoreName() != null ? setting.getStoreName() : "Alcateia")
                .storeDescription(setting.getStoreDescription())
                .storeFacadeUrl(setting.getStoreFacadeUrl())
                .storeExternalUrl(setting.getStoreExternalUrl())
                .storeIsOnlineActive(setting.getStoreIsOnlineActive() != null ? setting.getStoreIsOnlineActive() : false)
                .storeInstagramUrl(setting.getStoreInstagramUrl())
                .storeInstagramDescription(setting.getStoreInstagramDescription())
                .storeWhatsappNumber(setting.getStoreWhatsappNumber())
                .storeWhatsappDescription(setting.getStoreWhatsappDescription())
                .storeWhatsappMessage(setting.getStoreWhatsappMessage())
                .storeOpeningHours(setting.getStoreOpeningHours())
                .storeAddress(setting.getStoreAddress())
                .storeHasMemberDiscount(setting.getStoreHasMemberDiscount() != null ? setting.getStoreHasMemberDiscount() : true)
                .storeMemberDiscountText(setting.getStoreMemberDiscountText())
                .newsletterIsActive(setting.getNewsletterIsActive() != null ? setting.getNewsletterIsActive() : true)
                .createdAt(setting.getCreatedAt())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }
}
