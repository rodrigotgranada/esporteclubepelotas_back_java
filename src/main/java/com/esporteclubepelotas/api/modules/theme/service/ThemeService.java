package com.esporteclubepelotas.api.modules.theme.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.modules.theme.dto.ThemeRequestDto;
import com.esporteclubepelotas.api.modules.theme.dto.ThemeResponseDto;
import com.esporteclubepelotas.api.modules.theme.entity.Theme;
import com.esporteclubepelotas.api.modules.theme.entity.ThemePalette;
import com.esporteclubepelotas.api.modules.theme.helper.PaletteGeneratorHelper;
import com.esporteclubepelotas.api.modules.theme.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeRepository themeRepository;

    @Transactional(readOnly = true)
    public ThemeResponseDto getActiveTheme() {
        Instant now = Instant.now();

        // 1. Procura por campanhas ativas e vigentes no instante atual (UTC)
        List<Theme> activeCampaigns = themeRepository.findActiveCampaigns(now);
        if (!activeCampaigns.isEmpty()) {
            return ThemeResponseDto.fromEntity(activeCampaigns.get(0));
        }

        // 2. Fallback para tema regular padrão ativo
        return themeRepository.findFirstByIsActiveTrueAndIsCampaignFalse()
                .or(themeRepository::findFirstByIsDefaultTrue)
                .map(ThemeResponseDto::fromEntity)
                .orElseGet(() -> ThemeResponseDto.fromEntity(createDefaultThemeEntity()));
    }

    @Transactional(readOnly = true)
    public List<ThemeResponseDto> findAll() {
        return themeRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ThemeResponseDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ThemeResponseDto findById(Long id) {
        return ThemeResponseDto.fromEntity(findThemeOrThrow(id));
    }

    @Transactional
    public ThemeResponseDto create(ThemeRequestDto dto) {
        if (dto.getName() != null && themeRepository.existsByName(dto.getName())) {
            throw new BusinessException("Já existe um tema com este nome cadastrado: " + dto.getName());
        }

        boolean isCampaign = Boolean.TRUE.equals(dto.getIsCampaign());
        ThemePalette light = dto.getLight();
        ThemePalette dark = dto.getDark();

        // Se for campanha sazonal com cor semente e as paletas não foram enviadas
        // manualmente
        if (isCampaign && dto.getCampaignColor() != null && (light == null || dark == null)) {
            PaletteGeneratorHelper.GeneratedPalettes generated = PaletteGeneratorHelper
                    .generateCampaignPalettes(dto.getCampaignColor());
            if (light == null)
                light = generated.getLight();
            if (dark == null)
                dark = generated.getDark();
        }

        if (light == null)
            light = createDefaultLightPalette();
        if (dark == null)
            dark = createDefaultDarkPalette();

        boolean isActive = Boolean.TRUE.equals(dto.getIsActive());

        // Se estiver ativando um tema regular (não-campanha), desativa os outros temas
        // regulares
        if (isActive && !isCampaign) {
            deactivateAllRegularThemes();
        }

        Theme theme = Theme.builder()
                .name(dto.getName() != null ? dto.getName() : "Novo Tema")
                .isActive(isActive)
                .isDefault(Boolean.TRUE.equals(dto.getIsDefault()))
                .isCampaign(isCampaign)
                .campaignColor(dto.getCampaignColor())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .light(light)
                .dark(dark)
                .build();

        Theme saved = themeRepository.save(theme);
        return ThemeResponseDto.fromEntity(saved);
    }

    @Transactional
    public ThemeResponseDto update(Long id, ThemeRequestDto dto) {
        Theme theme = findThemeOrThrow(id);

        if (dto.getName() != null && !dto.getName().equalsIgnoreCase(theme.getName())) {
            if (themeRepository.existsByName(dto.getName())) {
                throw new BusinessException("Já existe outro tema cadastrado com o nome: " + dto.getName());
            }
            theme.setName(dto.getName());
        }

        if (dto.getIsCampaign() != null) {
            theme.setCampaign(dto.getIsCampaign());
        }

        if (dto.getCampaignColor() != null) {
            theme.setCampaignColor(dto.getCampaignColor());
            // Se as paletas não forem passadas explicitamente, regera com base na nova cor
            if (theme.isCampaign() && dto.getLight() == null && dto.getDark() == null) {
                PaletteGeneratorHelper.GeneratedPalettes gen = PaletteGeneratorHelper
                        .generateCampaignPalettes(dto.getCampaignColor());
                theme.setLight(gen.getLight());
                theme.setDark(gen.getDark());
            }
        }

        if (dto.getStartDate() != null)
            theme.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null)
            theme.setEndDate(dto.getEndDate());
        if (dto.getLight() != null)
            theme.setLight(dto.getLight());
        if (dto.getDark() != null)
            theme.setDark(dto.getDark());

        if (dto.getIsActive() != null) {
            boolean active = dto.getIsActive();
            if (active && !theme.isCampaign()) {
                deactivateAllRegularThemes();
            }
            theme.setActive(active);
        }

        Theme updated = themeRepository.save(theme);
        return ThemeResponseDto.fromEntity(updated);
    }

    @Transactional
    public void activate(Long id) {
        Theme theme = findThemeOrThrow(id);
        if (!theme.isCampaign()) {
            deactivateAllRegularThemes();
        }
        theme.setActive(true);
        themeRepository.save(theme);
    }

    @Transactional
    public void deactivate(Long id) {
        Theme theme = findThemeOrThrow(id);
        if (theme.isDefault()) {
            throw new BusinessException("O tema padrão do Esporte Clube Pelotas não pode ser desativado.");
        }
        theme.setActive(false);
        themeRepository.save(theme);
    }

    @Transactional
    public void delete(Long id) {
        Theme theme = findThemeOrThrow(id);
        if (theme.isDefault()) {
            throw new BusinessException("O tema padrão do Esporte Clube Pelotas não pode ser excluído.");
        }
        themeRepository.delete(theme);
    }

    @Transactional
    public void seedDefaultTheme() {
        if (themeRepository.count() == 0) {
            log.info("🌱 [ThemeService] Cadastrando Tema Padrão do Esporte Clube Pelotas...");
            createDefaultThemeEntity();
            log.info("✅ [ThemeService] Tema Padrão cadastrado com sucesso!");
        }
    }

    private void deactivateAllRegularThemes() {
        List<Theme> themes = themeRepository.findAll();
        for (Theme t : themes) {
            if (!t.isCampaign() && t.isActive()) {
                t.setActive(false);
                themeRepository.save(t);
            }
        }
    }

    private Theme findThemeOrThrow(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema não encontrado para o ID: " + id));
    }

    public static ThemePalette createDefaultLightPalette() {
        return ThemePalette.builder()
                .primary("#fbbf24")
                .secondary("#1e3a8a")
                .buttonSecondary("#1e3a8a")
                .background("#f8fafc")
                .surface("#ffffff")
                .header("#1e3a8a")
                .card("#ffffff")
                .drawer("#ffffff")
                .table("#ffffff")
                .textPrimary("#0f172a")
                .textSecondary("#475569")
                .border("#e2e8f0")
                .success("#22c55e")
                .error("#ea580c") // Terracota Alerta
                .warning("#f59e0b")
                .build();
    }

    public static ThemePalette createDefaultDarkPalette() {
        return ThemePalette.builder()
                .primary("#fbbf24")
                .secondary("#1e3a8a")
                .buttonSecondary("#1e3a8a")
                .background("#070e1c")
                .surface("#0d192f")
                .header("#091427")
                .card("#0d192f")
                .drawer("#091325")
                .table("#0d192f")
                .textPrimary("#f8fafc")
                .textSecondary("#94a3b8")
                .border("#1c2b48")
                .success("#22c55e")
                .error("#ea580c") // Terracota Alerta
                .warning("#f59e0b")
                .build();
    }

    private Theme createDefaultThemeEntity() {
        Theme defaultTheme = Theme.builder()
                .name("Padrão E.C. Pelotas")
                .isActive(true)
                .isDefault(true)
                .isCampaign(false)
                .light(createDefaultLightPalette())
                .dark(createDefaultDarkPalette())
                .build();
        return themeRepository.save(defaultTheme);
    }
}
