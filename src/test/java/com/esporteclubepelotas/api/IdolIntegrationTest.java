package com.esporteclubepelotas.api;

import com.esporteclubepelotas.api.modules.auth.dto.LoginRequestDto;
import com.esporteclubepelotas.api.modules.idol.dto.CreateAdminIdolDto;
import com.esporteclubepelotas.api.modules.idol.dto.UpdateAdminIdolDto;
import com.esporteclubepelotas.api.modules.idol.entity.IdolCategory;
import com.esporteclubepelotas.api.modules.idol.repository.IdolRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class IdolIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IdolRepository idolRepository;

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
    @DisplayName("Público deve listar ídolos do Memorial com paginação e flag do módulo ativa")
    void testPublicFindAll() throws Exception {
        mockMvc.perform(get("/idols?page=1&limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.isModuleActive").value(true));
    }

    @Test
    @DisplayName("Público deve listar os ídolos em destaque para a home")
    void testPublicFindFeatured() throws Exception {
        mockMvc.perform(get("/idols/featured?limit=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isFeatured").value(true));
    }

    @Test
    @DisplayName("Público deve obter detalhes de um ídolo por slug")
    void testPublicFindBySlug() throws Exception {
        mockMvc.perform(get("/idols/flavio-minuano"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Flávio Almeida da Silva"))
                .andExpect(jsonPath("$.nickname").value("Flávio Minuano"))
                .andExpect(jsonPath("$.category").value("PLAYER"));
    }

    @Test
    @DisplayName("Público deve navegar entre ídolo anterior e próximo na linha do tempo")
    void testPublicGetPrevNext() throws Exception {
        mockMvc.perform(get("/idols/luiz-carlos-winck/prev-next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.previous").exists())
                .andExpect(jsonPath("$.previous.slug").value("flavio-minuano"))
                .andExpect(jsonPath("$.next").exists())
                .andExpect(jsonPath("$.next.slug").value("seu-chico"));
    }

    @Test
    @DisplayName("Admin/Editor deve cadastrar um novo ídolo no Memorial com slug único")
    void testAdminCreateIdol() throws Exception {
        String editorToken = obtainToken("editor@esporteclubepelotas.com.br", "admin123");

        CreateAdminIdolDto createDto = CreateAdminIdolDto.builder()
                .name("Gabriel Leite")
                .nickname("Biel")
                .category(IdolCategory.PLAYER)
                .startYear(2010)
                .endYear(2015)
                .biography("Grande meia-atacante histórico com gols memoráveis na Divisão de Acesso.")
                .quote("Honrar esta camisa na Boca do Lobo foi o ápice da minha carreira.")
                .isFeatured(false)
                .order(10)
                .build();

        MvcResult result = mockMvc.perform(post("/idols/admin")
                        .header("Authorization", "Bearer " + editorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("biel"))
                .andExpect(jsonPath("$.name").value("Gabriel Leite"))
                .andReturn();

        JsonNode created = objectMapper.readTree(result.getResponse().getContentAsString());
        String createdId = created.get("id").asText();
        assertThat(createdId).isNotBlank();

        // Limpa após teste
        idolRepository.deleteById(createdId);
    }

    @Test
    @DisplayName("Admin deve atualizar dados de um ídolo existente")
    void testAdminUpdateIdol() throws Exception {
        String adminToken = obtainToken("admin@esporteclubepelotas.com.br", "admin123");
        var minuano = idolRepository.findBySlug("flavio-minuano").orElseThrow();

        UpdateAdminIdolDto updateDto = UpdateAdminIdolDto.builder()
                .quote("Pelotas é paixão sem limites na Boca do Lobo.")
                .build();

        mockMvc.perform(patch("/idols/admin/" + minuano.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quote").value("Pelotas é paixão sem limites na Boca do Lobo."));
    }

    @Test
    @DisplayName("Usuário comum (USER) não deve ter acesso a endpoints de /idols/admin (403 Forbidden)")
    void testNonAdminForbiddenOnAdminEndpoints() throws Exception {
        String userToken = obtainToken("torcedor@esporteclubepelotas.com.br", "admin123");

        mockMvc.perform(get("/idols/admin")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin deve realizar soft-delete de um ídolo")
    void testAdminSoftDelete() throws Exception {
        String ownerToken = obtainToken("admin@esporteclubepelotas.com.br", "admin123");

        // Cria ídolo temporário para exclusão
        CreateAdminIdolDto tempDto = CreateAdminIdolDto.builder()
                .name("Ídolo Temporário Para Teste")
                .category(IdolCategory.FAN)
                .startYear(2000)
                .biography("Biografia temporária de teste.")
                .build();

        MvcResult result = mockMvc.perform(post("/idols/admin")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tempDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String tempId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

        // Executa soft-delete
        mockMvc.perform(delete("/idols/admin/" + tempId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Confirma que não aparece mais na listagem pública
        var deleted = idolRepository.findById(tempId).orElseThrow();
        assertThat(deleted.isActive()).isFalse();

        // Limpa
        idolRepository.deleteById(tempId);
    }
}

