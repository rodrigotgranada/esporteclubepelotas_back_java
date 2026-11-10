package com.esporteclubepelotas.api.modules.setting.entity;

import com.esporteclubepelotas.api.modules.setting.model.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "club_logo_url", columnDefinition = "TEXT")
    private String clubLogoUrl;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "club_logo_gallery")
    private List<String> clubLogoGallery = new ArrayList<>();

    @Builder.Default
    @Column(name = "club_name", nullable = false)
    private String clubName = "Esporte Clube Pelotas";

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "social_links")
    private List<SocialLinkItem> socialLinks = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_phones")
    private List<ContactPhoneItem> contactPhones = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_emails")
    private List<ContactEmailItem> contactEmails = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "opening_hours")
    private List<OpeningHourItem> openingHours = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "footer_navigation_links")
    private List<FooterNavigationItem> footerNavigationLinks = new ArrayList<>();

    @Builder.Default
    @Column(name = "stadium_name")
    private String stadiumName = "Estádio Boca do Lobo";

    @Builder.Default
    @Column(name = "stadium_address", length = 500)
    private String stadiumAddress = "Av. Bento Gonçalves esquina Parque Dom Antônio Zattera - Pelotas/RS, CEP 96015-140";

    @Builder.Default
    @Column(name = "google_maps_embed_url", columnDefinition = "TEXT")
    private String googleMapsEmbedUrl = "https://maps.google.com/maps?q=Est%C3%A1dio+Boca+do+Lobo,+Pelotas+-+RS&t=&z=16&ie=UTF8&iwloc=&output=embed";

    @Builder.Default
    @Column(name = "copyright_text")
    private String copyrightText = "© 2026 Esporte Clube Pelotas. Todos os direitos reservados.";

    @Builder.Default
    @Column(name = "membership_system_provider", nullable = false, length = 50)
    private String membershipSystemProvider = "ELEVENTICKETS";

    @Builder.Default
    @Column(name = "membership_external_url", columnDefinition = "TEXT", nullable = false)
    private String membershipExternalUrl = "https://socioecpelotas.eleventickets.com/#/";

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "youth_pillars")
    private List<YouthPillarItem> youthPillars = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "youth_tryouts")
    private YouthTryoutsConfig youthTryouts = new YouthTryoutsConfig();

    // ==========================================
    // LOJA OFICIAL LOBO MANIA
    // ==========================================
    @Builder.Default
    @Column(name = "store_name", length = 150)
    private String storeName = "Loja Oficial Lobo Mania";

    @Builder.Default
    @Column(name = "store_description", columnDefinition = "TEXT")
    private String storeDescription = "Vista o manto áureo-cerúleo e apoie o Esporte Clube Pelotas. Produtos oficiais, camisas de jogo, linha casual e acessórios exclusivos.";

    @Column(name = "store_external_url", length = 500)
    private String storeExternalUrl;

    @Builder.Default
    @Column(name = "store_is_online_active")
    private Boolean storeIsOnlineActive = false;

    @Builder.Default
    @Column(name = "store_instagram_url", length = 255)
    private String storeInstagramUrl = "https://www.instagram.com/lojadolobo/";

    @Builder.Default
    @Column(name = "store_whatsapp_number", length = 50)
    private String storeWhatsappNumber = "5553999999999";

    @Builder.Default
    @Column(name = "store_whatsapp_message", length = 255)
    private String storeWhatsappMessage = "Olá! Gostaria de informações sobre produtos e mantos oficiais do E.C. Pelotas.";

    @Builder.Default
    @Column(name = "store_opening_hours", length = 255)
    private String storeOpeningHours = "Segunda a Sexta: 09h às 19h | Sábado: 09h às 13h | Dias de jogos na Boca do Lobo: até o intervalo";

    @Builder.Default
    @Column(name = "store_address", length = 255)
    private String storeAddress = "Estádio Boca do Lobo - Av. Bento Gonçalves esquina Parque Dom Antônio Zattera - Pelotas/RS";

    @Builder.Default
    @Column(name = "store_has_member_discount")
    private Boolean storeHasMemberDiscount = true;

    @Builder.Default
    @Column(name = "store_member_discount_text", length = 255)
    private String storeMemberDiscountText = "Sócios do Pelotas em dia têm desconto exclusivo em produtos e mantos oficiais mediante apresentação da carteirinha física ou digital.";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
