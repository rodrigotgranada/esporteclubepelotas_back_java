package com.esporteclubepelotas.api;

import com.esporteclubepelotas.api.modules.auth.dto.LoginRequestDto;
import com.esporteclubepelotas.api.modules.football.dto.*;
import com.esporteclubepelotas.api.modules.football.entity.PreferredFoot;
import com.esporteclubepelotas.api.modules.football.entity.SquadCategory;
import com.esporteclubepelotas.api.modules.football.entity.SquadMemberRole;
import com.esporteclubepelotas.api.modules.football.entity.SquadSector;
import com.esporteclubepelotas.api.modules.football.repository.AthleteRepository;
import com.esporteclubepelotas.api.modules.football.repository.SquadRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FootballIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AthleteRepository athleteRepository;

    @Autowired
    private SquadRepository squadRepository;

    private String obtainToken(String identifier, String password) throws Exception {
        LoginRequestDto loginDto = LoginRequestDto.builder()
                .cpf(identifier)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("accessToken").asText();
    }

    @Test
    @DisplayName("Público deve listar atletas com paginação e busca")
    void testPublicFindAllAthletes() throws Exception {
        mockMvc.perform(get("/athletes?page=1&limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.isModuleActive").value(true));
    }

    @Test
    @DisplayName("Público deve obter ficha técnica do atleta por slug com idade calculada e histórico de temporadas")
    void testPublicFindAthleteBySlug() throws Exception {
        mockMvc.perform(get("/athletes/walter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Walter Henrique de Oliveira"))
                .andExpect(jsonPath("$.nickname").value("Walter"))
                .andExpect(jsonPath("$.slug").value("walter"))
                .andExpect(jsonPath("$.primaryPosition").value("Centroavante"))
                .andExpect(jsonPath("$.age").isNumber())
                .andExpect(jsonPath("$.seasons").isArray());
    }

    @Test
    @DisplayName("Público deve consultar temporadas e categorias disponíveis")
    void testPublicSquadsSeasons() throws Exception {
        mockMvc.perform(get("/squads/seasons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.years").isArray())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.isModuleActive").value(true));
    }

    @Test
    @DisplayName("Público deve obter elenco atual organizado por setores táticos")
    void testPublicCurrentSquadWithSectors() throws Exception {
        mockMvc.perform(get("/squads/current?category=PROFESSIONAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").isNumber())
                .andExpect(jsonPath("$.category").value("PROFESSIONAL"))
                .andExpect(jsonPath("$.isCurrent").value(true))
                .andExpect(jsonPath("$.sectors.goalkeepers").isArray())
                .andExpect(jsonPath("$.sectors.defenders").isArray())
                .andExpect(jsonPath("$.sectors.midfielders").isArray())
                .andExpect(jsonPath("$.sectors.forwards").isArray())
                .andExpect(jsonPath("$.sectors.forwards[0].athlete.nickname").value("Walter"));
    }

    @Test
    @DisplayName("Público deve obter elenco de ano e categoria específicos")
    void testPublicSquadByYearAndCategory() throws Exception {
        mockMvc.perform(get("/squads/2026/PROFESSIONAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2026))
                .andExpect(jsonPath("$.category").value("PROFESSIONAL"))
                .andExpect(jsonPath("$.seasonName").isNotEmpty());
    }

    @Test
    @DisplayName("Admin deve cadastrar, atualizar e inativar atleta no banco permanente")
    void testAdminCreateAndUpdateAthlete() throws Exception {
        String adminToken = obtainToken("admin@esporteclubepelotas.com.br", "admin123");

        CreateAthleteDto createDto = CreateAthleteDto.builder()
                .name("Gabriel Leite")
                .nickname("Biel")
                .primaryPosition("Meia-Atacante")
                .preferredFoot(PreferredFoot.LEFT)
                .birthDate("2001-05-18")
                .birthPlace("Pelotas - RS")
                .height(176)
                .weight(71)
                .build();

        // 1. Cadastrar atleta
        MvcResult createResult = mockMvc.perform(post("/athletes/admin")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Gabriel Leite"))
                .andExpect(jsonPath("$.slug").value("biel"))
                .andReturn();

        JsonNode createdNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String athleteId = createdNode.get("id").asText();

        // 2. Atualizar atleta
        UpdateAthleteDto updateDto = UpdateAthleteDto.builder()
                .instagram("biel_leite")
                .build();

        mockMvc.perform(patch("/athletes/admin/" + athleteId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instagram").value("biel_leite"));

        // 3. Inativar atleta (soft-delete)
        mockMvc.perform(delete("/athletes/admin/" + athleteId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Admin deve criar elenco, definir membros e atualizar alocação tática")
    void testAdminSquadManagementAndMembers() throws Exception {
        String adminToken = obtainToken("admin@esporteclubepelotas.com.br", "admin123");
        var walter = athleteRepository.findBySlug("walter").orElseThrow();

        CreateSquadDto squadDto = CreateSquadDto.builder()
                .year(2027)
                .category(SquadCategory.PROFESSIONAL)
                .seasonName("Temporada 2027 - Copa do Brasil")
                .coachName("Leocir Dall’Astra")
                .isCurrent(false)
                .build();

        // 1. Criar novo elenco
        MvcResult squadResult = mockMvc.perform(post("/squads/admin")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(squadDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.year").value(2027))
                .andReturn();

        JsonNode squadNode = objectMapper.readTree(squadResult.getResponse().getContentAsString());
        String squadId = squadNode.get("id").asText();

        // 2. Alocar membro no elenco
        SquadMemberDto memberDto = SquadMemberDto.builder()
                .athleteId(walter.getId().toString())
                .jerseyNumber(9)
                .sector(SquadSector.FORWARD)
                .role(SquadMemberRole.CAPTAIN)
                .order(1)
                .build();

        mockMvc.perform(post("/squads/admin/" + squadId + "/members")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members[0].jerseyNumber").value(9));

        // 3. Remover membro do elenco
        mockMvc.perform(delete("/squads/admin/" + squadId + "/members/" + walter.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 4. Inativar elenco
        mockMvc.perform(delete("/squads/admin/" + squadId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Usuário comum (USER) não deve ter permissão para acessar o admin de futebol")
    void testSecurityPermissions() throws Exception {
        String userToken = obtainToken("torcedor@esporteclubepelotas.com.br", "admin123");

        CreateAthleteDto dto = CreateAthleteDto.builder()
                .name("Jogador Invasor")
                .primaryPosition("Atacante")
                .build();

        mockMvc.perform(post("/athletes/admin")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}

