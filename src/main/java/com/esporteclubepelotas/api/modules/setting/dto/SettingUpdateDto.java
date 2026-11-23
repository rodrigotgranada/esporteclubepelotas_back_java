package com.esporteclubepelotas.api.modules.setting.dto;

import com.esporteclubepelotas.api.modules.setting.model.*;
import jakarta.validation.constraints.Size;
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
    private Boolean developerCreditEnabled;

    @Size(max = 120, message = "O nome de exibição do desenvolvedor deve ter no máximo 120 caracteres.")
    private String developerCreditName;

    @Size(max = 500, message = "O link do desenvolvedor deve ter no máximo 500 caracteres.")
    private String developerCreditUrl;
}
