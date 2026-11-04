package com.esporteclubepelotas.api.modules.setting.service;

import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.modules.setting.dto.SettingResponseDto;
import com.esporteclubepelotas.api.modules.setting.dto.SettingUpdateDto;
import com.esporteclubepelotas.api.modules.setting.entity.Setting;
import com.esporteclubepelotas.api.modules.setting.model.*;
import com.esporteclubepelotas.api.modules.setting.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;
    private final StorageService storageService;

    @Transactional
    public SettingResponseDto getSettings() {
        Setting setting = settingRepository.findFirstByOrderByIdAsc()
                .orElseGet(this::createDefaultSettings);
        return SettingResponseDto.fromEntity(setting);
    }

    @Transactional
    public SettingResponseDto updateSettings(SettingUpdateDto dto, MultipartFile logoFile) {
        Setting setting = settingRepository.findFirstByOrderByIdAsc()
                .orElseGet(this::createDefaultSettings);

        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                String originalFilename = logoFile.getOriginalFilename() != null ? logoFile.getOriginalFilename() : "logo.png";
                String logoUrl = storageService.uploadFile(
                        logoFile.getBytes(),
                        "club-logo-" + System.currentTimeMillis() + "-" + originalFilename,
                        "settings",
                        logoFile.getContentType()
                );
                setting.setClubLogoUrl(logoUrl);

                List<String> currentGallery = setting.getClubLogoGallery() != null
                        ? new ArrayList<>(setting.getClubLogoGallery())
                        : new ArrayList<>();
                if (!currentGallery.contains(logoUrl)) {
                    currentGallery.add(0, logoUrl);
                }
                setting.setClubLogoGallery(currentGallery);
            } catch (IOException e) {
                log.error("Erro ao ler bytes do arquivo de logo: {}", e.getMessage(), e);
                throw new RuntimeException("Falha ao processar arquivo de logo", e);
            }
        }

        if (dto != null) {
            if (dto.getClubName() != null) setting.setClubName(dto.getClubName());
            if (dto.getClubLogoUrl() != null) setting.setClubLogoUrl(dto.getClubLogoUrl());
            if (dto.getClubLogoGallery() != null) setting.setClubLogoGallery(new ArrayList<>(dto.getClubLogoGallery()));
            if (dto.getSocialLinks() != null) setting.setSocialLinks(new ArrayList<>(dto.getSocialLinks()));
            if (dto.getContactPhones() != null) setting.setContactPhones(new ArrayList<>(dto.getContactPhones()));
            if (dto.getContactEmails() != null) setting.setContactEmails(new ArrayList<>(dto.getContactEmails()));
            if (dto.getOpeningHours() != null) setting.setOpeningHours(new ArrayList<>(dto.getOpeningHours()));
            if (dto.getFooterNavigationLinks() != null) setting.setFooterNavigationLinks(new ArrayList<>(dto.getFooterNavigationLinks()));
            if (dto.getStadiumName() != null) setting.setStadiumName(dto.getStadiumName());
            if (dto.getStadiumAddress() != null) setting.setStadiumAddress(dto.getStadiumAddress());
            if (dto.getGoogleMapsEmbedUrl() != null) setting.setGoogleMapsEmbedUrl(dto.getGoogleMapsEmbedUrl());
            if (dto.getCopyrightText() != null) setting.setCopyrightText(dto.getCopyrightText());
        }

        Setting saved = settingRepository.save(setting);
        return SettingResponseDto.fromEntity(saved);
    }

    @Transactional
    public void seedDefaultSettings() {
        if (settingRepository.count() == 0) {
            log.info("🌱 [SettingService] Cadastrando Configurações Globais Padrão do Portal...");
            createDefaultSettings();
            log.info("✅ [SettingService] Configurações Padrão cadastradas com sucesso!");
        }
    }

    private Setting createDefaultSettings() {
        List<SocialLinkItem> socialLinks = List.of(
                SocialLinkItem.builder().platform("instagram").label("Instagram").url("https://instagram.com/ecpelotas").build(),
                SocialLinkItem.builder().platform("facebook").label("Facebook").url("https://facebook.com/ecpelotas").build(),
                SocialLinkItem.builder().platform("x").label("X (Twitter)").url("https://x.com/ecpelotas").build(),
                SocialLinkItem.builder().platform("youtube").label("YouTube").url("https://youtube.com/@ecpelotastv").build()
        );

        List<ContactPhoneItem> phones = List.of(
                ContactPhoneItem.builder().label("Secretaria Geral").number("(53) 3222-3022").isWhatsapp(false).build(),
                ContactPhoneItem.builder().label("Central de Sócios (WhatsApp)").number("(53) 99999-1908").isWhatsapp(true).build()
        );

        List<ContactEmailItem> emails = List.of(
                ContactEmailItem.builder().label("Contato Geral").email("contato@esporteclubepelotas.com.br").build(),
                ContactEmailItem.builder().label("Secretaria / Sócios").email("socios@esporteclubepelotas.com.br").build()
        );

        List<OpeningHourItem> openingHours = List.of(
                OpeningHourItem.builder().department("Secretaria & Loja Oficial").schedule("Segunda a Sexta: 09:00 às 18:00 | Sábado: 09:00 às 12:30").build()
        );

        List<FooterNavigationItem> footerLinks = List.of(
                FooterNavigationItem.builder().label("O Clube").url("/historia").isVisible(true).isExternal(false).build(),
                FooterNavigationItem.builder().label("Elenco Atual").url("/futebol").isVisible(true).isExternal(false).build(),
                FooterNavigationItem.builder().label("Quadro Social").url("/socio").isVisible(true).isExternal(false).build(),
                FooterNavigationItem.builder().label("Galeria de Ídolos").url("/idolos").isVisible(true).isExternal(false).build(),
                FooterNavigationItem.builder().label("Notícias").url("/noticias").isVisible(true).isExternal(false).build()
        );

        Setting setting = Setting.builder()
                .clubName("Esporte Clube Pelotas")
                .clubLogoUrl(null)
                .clubLogoGallery(new ArrayList<>())
                .socialLinks(socialLinks)
                .contactPhones(phones)
                .contactEmails(emails)
                .openingHours(openingHours)
                .footerNavigationLinks(footerLinks)
                .stadiumName("Estádio Boca do Lobo")
                .stadiumAddress("Av. Bento Gonçalves esquina Parque Dom Antônio Zattera - Pelotas/RS, CEP 96015-140")
                .googleMapsEmbedUrl("https://maps.google.com/maps?q=Est%C3%A1dio+Boca+do+Lobo,+Pelotas+-+RS&t=&z=16&ie=UTF8&iwloc=&output=embed")
                .copyrightText("© 2026 Esporte Clube Pelotas. Todos os direitos reservados.")
                .build();

        return settingRepository.save(setting);
    }
}

