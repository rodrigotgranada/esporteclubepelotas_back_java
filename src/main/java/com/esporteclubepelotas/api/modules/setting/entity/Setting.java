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

    @Column(name = "club_logo_url")
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
    @Column(name = "google_maps_embed_url", length = 1000)
    private String googleMapsEmbedUrl = "https://maps.google.com/maps?q=Est%C3%A1dio+Boca+do+Lobo,+Pelotas+-+RS&t=&z=16&ie=UTF8&iwloc=&output=embed";

    @Builder.Default
    @Column(name = "copyright_text")
    private String copyrightText = "© 2026 Esporte Clube Pelotas. Todos os direitos reservados.";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
