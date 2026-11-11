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
}
