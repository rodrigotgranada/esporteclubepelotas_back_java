package com.esporteclubepelotas.api.modules.football.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.modules.football.dto.YouthPillarDto;
import com.esporteclubepelotas.api.modules.football.dto.YouthPortalConfigDto;
import com.esporteclubepelotas.api.modules.football.entity.YouthPillarItem;
import com.esporteclubepelotas.api.modules.football.entity.YouthPortalSetting;
import com.esporteclubepelotas.api.modules.football.repository.YouthPortalSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YouthPortalService {

    private static final Long SINGLETON_ID = 1L;
    private static final int MAX_PILLARS = 4;
    private static final int MAX_PILLAR_TITLE = 60;
    private static final int MAX_PILLAR_DESCRIPTION = 220;

    private final YouthPortalSettingRepository repository;

    @Transactional(readOnly = true)
    public YouthPortalConfigDto getConfig() {
        YouthPortalSetting setting = repository.findById(SINGLETON_ID)
                .orElseGet(this::buildDefaultEntity);
        return mapToDto(setting);
    }

    @Transactional
    public YouthPortalConfigDto updateConfig(YouthPortalConfigDto dto) {
        List<YouthPillarDto> incomingPillars = dto.getPillars() != null ? dto.getPillars() : Collections.emptyList();
        if (incomingPillars.size() > MAX_PILLARS) {
            throw new BusinessException("É permitido cadastrar no máximo 4 pilares do futebol de base.");
        }

        List<YouthPillarItem> sanitizedPillars = new ArrayList<>();
        for (YouthPillarDto p : incomingPillars) {
            if (p == null) continue;
            String title = p.getTitle() != null ? p.getTitle().trim() : "";
            String desc = p.getDescription() != null ? p.getDescription().trim() : "";
            if (title.isEmpty() && desc.isEmpty()) continue;

            if (title.isEmpty()) {
                throw new BusinessException("O título do pilar é obrigatório.");
            }
            if (title.length() > MAX_PILLAR_TITLE) {
                throw new BusinessException("O título do pilar deve ter no máximo " + MAX_PILLAR_TITLE + " caracteres.");
            }
            if (desc.isEmpty()) {
                throw new BusinessException("O texto do pilar é obrigatório.");
            }
            if (desc.length() > MAX_PILLAR_DESCRIPTION) {
                throw new BusinessException("O texto do pilar deve ter no máximo " + MAX_PILLAR_DESCRIPTION + " caracteres.");
            }

            sanitizedPillars.add(YouthPillarItem.builder()
                    .title(title)
                    .description(desc)
                    .build());
        }

        List<String> sanitizedCriteria = new ArrayList<>();
        if (dto.getPeneirasCriteria() != null) {
            for (String item : dto.getPeneirasCriteria()) {
                if (item != null && !item.trim().isEmpty()) {
                    String trimmed = item.trim();
                    if (trimmed.length() > 350) {
                        trimmed = trimmed.substring(0, 350);
                    }
                    sanitizedCriteria.add(trimmed);
                }
            }
        }

        YouthPortalSetting setting = repository.findById(SINGLETON_ID)
                .orElseGet(() -> YouthPortalSetting.builder().id(SINGLETON_ID).build());

        setting.setPillars(sanitizedPillars);
        setting.setPeneirasCriteria(sanitizedCriteria);
        setting.setContactTitle(
                dto.getContactTitle() != null && !dto.getContactTitle().isBlank()
                        ? dto.getContactTitle().trim()
                        : "Departamento de Futebol de Base"
        );
        setting.setContactDescription(
                dto.getContactDescription() != null ? dto.getContactDescription().trim() : null
        );
        setting.setContactEmail(
                dto.getContactEmail() != null && !dto.getContactEmail().isBlank()
                        ? dto.getContactEmail().trim()
                        : null
        );
        setting.setContactPhone(
                dto.getContactPhone() != null && !dto.getContactPhone().isBlank()
                        ? dto.getContactPhone().trim()
                        : null
        );

        YouthPortalSetting saved = repository.save(setting);
        return mapToDto(saved);
    }

    private YouthPortalConfigDto mapToDto(YouthPortalSetting entity) {
        List<YouthPillarDto> pillarDtos = (entity.getPillars() != null ? entity.getPillars() : Collections.<YouthPillarItem>emptyList())
                .stream()
                .map(p -> YouthPillarDto.builder()
                        .title(p.getTitle())
                        .description(p.getDescription())
                        .build())
                .toList();

        List<String> criteria = entity.getPeneirasCriteria() != null
                ? new ArrayList<>(entity.getPeneirasCriteria())
                : new ArrayList<>();

        return YouthPortalConfigDto.builder()
                .pillars(new ArrayList<>(pillarDtos))
                .peneirasCriteria(criteria)
                .contactTitle(entity.getContactTitle() != null ? entity.getContactTitle() : "Departamento de Futebol de Base")
                .contactDescription(entity.getContactDescription())
                .contactEmail(entity.getContactEmail())
                .contactPhone(entity.getContactPhone())
                .build();
    }

    private YouthPortalSetting buildDefaultEntity() {
        return YouthPortalSetting.builder()
                .id(SINGLETON_ID)
                .pillars(List.of(
                        YouthPillarItem.builder()
                                .title("DNA Competitivo Áureo-Cerúleo")
                                .description("Ensinamos o respeito à história centenária do Pelotas, a dedicação intransigente em campo e o orgulho de defender as cores azul e amarela na Boca do Lobo.")
                                .build(),
                        YouthPillarItem.builder()
                                .title("Metodologia Tática & Intensidade")
                                .description("Treinamentos modernos focados em rápida leitura de jogo, tomada de decisão sob pressão, transições velozes e alto rigor físico.")
                                .build(),
                        YouthPillarItem.builder()
                                .title("Acompanhamento Cidadão & Escolar")
                                .description("O atleta do Pelotas deve manter bom rendimento escolar comprovado. Oferecemos suporte pedagógico e orientação para a vida fora das quatro linhas.")
                                .build(),
                        YouthPillarItem.builder()
                                .title("Integração com o Profissional")
                                .description("Comunicação direta entre as comissões técnicas da base e a equipe principal, possibilitando treinos integrados e promoção gradual dos jovens talentos.")
                                .build()
                ))
                .peneirasCriteria(List.of(
                        "Atestado médico cardiológico de aptidão física original (emitido nos últimos 6 meses).",
                        "Documento de identidade oficial com foto (RG) e CPF do atleta e do responsável.",
                        "Declaração de matrícula e frequência escolar ativa no ano letivo corrente.",
                        "Material de treino próprio: chuteiras, caneleiras, meiões e calção (azul, amarelo, branco ou preto; proibido uniforme de rivais).",
                        "Termo de autorização de participação preenchido e assinado pelos pais ou responsáveis legais."
                ))
                .contactTitle("Departamento de Futebol de Base")
                .contactDescription("As datas oficiais de avaliações abertas e peneiras são divulgadas periodicamente nos canais oficiais do clube. Fique atento às redes sociais do Pelotas!")
                .contactEmail("base@esporteclubepelotas.com.br")
                .contactPhone(null)
                .build();
    }
}
