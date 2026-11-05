package com.esporteclubepelotas.api.modules.football.service;

import com.esporteclubepelotas.api.modules.football.entity.*;
import com.esporteclubepelotas.api.modules.football.repository.AthleteRepository;
import com.esporteclubepelotas.api.modules.football.repository.SquadMemberRepository;
import com.esporteclubepelotas.api.modules.football.repository.SquadRepository;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FootballSeeder {

    private final AthleteRepository athleteRepository;
    private final SquadRepository squadRepository;
    private final SquadMemberRepository squadMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public void seedDefaultFootball() {
        if (athleteRepository.count() > 0) {
            return;
        }

        log.info("🌱 [FootballSeeder] Criando Atletas e Elenco Profissional 2026 oficial...");

        User author = userRepository.findByEmail("admin@esporteclubepelotas.com.br")
                .or(() -> userRepository.findAll().stream().findFirst())
                .orElse(null);

        // 1. Goleiro
        Athlete rodrigo = Athlete.builder()
                .name("Rodrigo Costa de Souza")
                .nickname("Rodrigo")
                .slug("rodrigo-costa")
                .primaryPosition("Goleiro")
                .secondaryPositions(List.of())
                .preferredFoot(PreferredFoot.RIGHT)
                .birthDate(LocalDate.of(1995, 4, 12))
                .birthPlace("Pelotas - RS")
                .height(190)
                .weight(86)
                .photoUrl("https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=600&q=80")
                .pastClubs(List.of("Brasil de Farroupilha", "São José-RS"))
                .biography("Goleiro seguro e de grande liderança dentro da grande área, formado nas categorias de base do futebol gaúcho.")
                .isActive(true)
                .createdBy(author)
                .build();

        // 2. Defensor
        Athlete heitor = Athlete.builder()
                .name("Heitor Santos Silveira")
                .nickname("Heitor")
                .slug("heitor-santos")
                .primaryPosition("Zagueiro")
                .secondaryPositions(List.of("Lateral-Direito"))
                .preferredFoot(PreferredFoot.RIGHT)
                .birthDate(LocalDate.of(1996, 8, 20))
                .birthPlace("Porto Alegre - RS")
                .height(187)
                .weight(82)
                .photoUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=600&q=80")
                .pastClubs(List.of("Caxias", "Ypiranga"))
                .biography("Zagueiro firme no jogo aéreo e com excelente tempo de bola nas coberturas defensivas.")
                .isActive(true)
                .createdBy(author)
                .build();

        // 3. Meio-campista
        Athlete carlinhos = Athlete.builder()
                .name("Carlos Alberto Ferreira")
                .nickname("Carlinhos")
                .slug("carlinhos")
                .primaryPosition("Volante")
                .secondaryPositions(List.of("Meio-Campista"))
                .preferredFoot(PreferredFoot.RIGHT)
                .birthDate(LocalDate.of(1998, 2, 15))
                .birthPlace("Rio Grande - RS")
                .height(179)
                .weight(75)
                .photoUrl("https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=600&q=80")
                .pastClubs(List.of("Avenida", "Aimoré"))
                .biography("Volante moderno com alta taxa de desarmes e precisão na transição da defesa para o ataque.")
                .isActive(true)
                .createdBy(author)
                .build();

        // 4. Centroavante (Walter)
        Athlete walter = Athlete.builder()
                .name("Walter Henrique de Oliveira")
                .nickname("Walter")
                .slug("walter")
                .primaryPosition("Centroavante")
                .secondaryPositions(List.of("Segundo Atacante"))
                .preferredFoot(PreferredFoot.RIGHT)
                .birthDate(LocalDate.of(1989, 7, 22))
                .birthPlace("Recife - PE")
                .height(178)
                .weight(85)
                .photoUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=600&q=80")
                .pastClubs(List.of("Internacional", "Porto", "Goiás", "Fluminense", "Athletico-PR", "Goiás"))
                .biography("Atacante consagrado nacionalmente com passagem marcante por grandes clubes brasileiros e títulos internacionais. Referência técnica e ídolo da torcida áureo-cerúlea na Boca do Lobo.")
                .instagram("walter_oficial")
                .isActive(true)
                .createdBy(author)
                .build();

        athleteRepository.saveAll(List.of(rodrigo, heitor, carlinhos, walter));

        // 2. Elenco Profissional 2026
        Squad squad2026 = Squad.builder()
                .year(2026)
                .category(SquadCategory.PROFESSIONAL)
                .seasonName("Temporada 2026 - Gauchão Série A")
                .coachName("Leocir Dall’Astra")
                .staff(List.of(
                        SquadStaffMember.builder().role("Auxiliar Técnico").name("Felipe Endres").build(),
                        SquadStaffMember.builder().role("Preparador Físico").name("Lucas Pereira").build()
                ))
                .coverUrl("https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=1200&q=80")
                .isCurrent(true)
                .isActive(true)
                .createdBy(author)
                .build();

        Squad savedSquad = squadRepository.save(squad2026);

        SquadMember m1 = SquadMember.builder()
                .squad(savedSquad)
                .athlete(rodrigo)
                .jerseyNumber(1)
                .sector(SquadSector.GOALKEEPER)
                .role(SquadMemberRole.STARTER)
                .displayOrder(1)
                .build();

        SquadMember m2 = SquadMember.builder()
                .squad(savedSquad)
                .athlete(heitor)
                .jerseyNumber(3)
                .sector(SquadSector.DEFENDER)
                .role(SquadMemberRole.STARTER)
                .displayOrder(2)
                .build();

        SquadMember m3 = SquadMember.builder()
                .squad(savedSquad)
                .athlete(carlinhos)
                .jerseyNumber(5)
                .sector(SquadSector.MIDFIELDER)
                .role(SquadMemberRole.STARTER)
                .displayOrder(3)
                .build();

        SquadMember m4 = SquadMember.builder()
                .squad(savedSquad)
                .athlete(walter)
                .jerseyNumber(9)
                .sector(SquadSector.FORWARD)
                .role(SquadMemberRole.CAPTAIN)
                .displayOrder(4)
                .build();

        squadMemberRepository.saveAll(List.of(m1, m2, m3, m4));

        log.info("✅ [FootballSeeder] 4 Atletas e Elenco 2026 com membros alocados nos 4 setores cadastrados com sucesso.");
    }
}

