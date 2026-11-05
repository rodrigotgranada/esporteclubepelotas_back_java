package com.esporteclubepelotas.api;

import com.esporteclubepelotas.api.modules.auth.dto.LoginRequestDto;
import com.esporteclubepelotas.api.modules.news.dto.*;
import com.esporteclubepelotas.api.modules.news.entity.NewsFeaturedSlot;
import com.esporteclubepelotas.api.modules.news.entity.NewsReactionType;
import com.esporteclubepelotas.api.modules.news.entity.NewsStatus;
import com.esporteclubepelotas.api.modules.news.repository.NewsArticleRepository;
import com.esporteclubepelotas.api.modules.news.repository.NewsCategoryRepository;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NewsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NewsArticleRepository articleRepository;

    @Autowired
    private NewsCategoryRepository categoryRepository;

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
    @DisplayName("Público deve listar feed de notícias com paginação e ordenação áureo-cerúlea")
    void testPublicFeed() throws Exception {
        mockMvc.perform(get("/news?page=1&limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10));
    }

    @Test
    @DisplayName("Público deve obter matérias em destaque para carrossel e vitrine da Home")
    void testPublicFeatured() throws Exception {
        mockMvc.perform(get("/news/featured?limit=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").isNotEmpty())
                .andExpect(jsonPath("$[0].isFeatured").value(true));
    }

    @Test
    @DisplayName("Público deve listar categorias oficiais ativas")
    void testPublicCategories() throws Exception {
        mockMvc.perform(get("/news/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].slug").isNotEmpty())
                .andExpect(jsonPath("$[0].name").isNotEmpty());
    }

    @Test
    @DisplayName("Público deve ler matéria por slug e consultar navegação anterior/próxima")
    void testFindBySlugAndPrevNext() throws Exception {
        String slug = "pelotas-vence-o-classico-farroupilha-por-2-a-0-na-boca-do-lobo";

        mockMvc.perform(get("/news/" + slug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Pelotas vence o clássico Farroupilha por 2 a 0 na Boca do Lobo"))
                .andExpect(jsonPath("$.category.slug").value("futebol-profissional"))
                .andExpect(jsonPath("$.viewsCount").isNumber());

        mockMvc.perform(get("/news/" + slug + "/prev-next"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Torcedor logado deve reagir à matéria e consultar sua reação")
    void testReactions() throws Exception {
        String token = obtainToken("torcedor@esporteclubepelotas.com.br", "admin123");
        String slug = "pelotas-vence-o-classico-farroupilha-por-2-a-0-na-boca-do-lobo";

        ReactNewsDto reactDto = ReactNewsDto.builder()
                .type(NewsReactionType.WOLF)
                .build();

        // 1. Reagir com Lobo
        mockMvc.perform(post("/news/" + slug + "/reactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reactions.wolf").isNumber())
                .andExpect(jsonPath("$.userReaction").value("wolf"));

        // 2. Consultar minha reação
        mockMvc.perform(get("/news/" + slug + "/my-reaction")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userReaction").value("wolf"));

        // 3. Toggle off: desmarcar reação
        mockMvc.perform(post("/news/" + slug + "/reactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userReaction").doesNotExist());
    }

    @Test
    @DisplayName("Torcedor logado deve adicionar comentário e Admin deve moderar")
    void testCommentsAndModeration() throws Exception {
        String userToken = obtainToken("torcedor@esporteclubepelotas.com.br", "admin123");
        String adminToken = obtainToken("admin@esporteclubepelotas.com.br", "admin123");
        String slug = "pelotas-vence-o-classico-farroupilha-por-2-a-0-na-boca-do-lobo";

        CreateNewsCommentDto commentDto = CreateNewsCommentDto.builder()
                .content("Bela vitória do Lobo na Boca do Lobo! Rumo ao título!")
                .build();

        // 1. Adicionar comentário
        MvcResult commentResult = mockMvc.perform(post("/news/" + slug + "/comments")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Bela vitória do Lobo na Boca do Lobo! Rumo ao título!"))
                .andReturn();

        JsonNode commentJson = objectMapper.readTree(commentResult.getResponse().getContentAsString());
        String commentId = commentJson.get("id").asText();

        // 2. Listar comentários públicos da matéria
        mockMvc.perform(get("/news/" + slug + "/comments?page=1&limit=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.total").isNumber());

        // 3. Admin modera o status para HIDDEN
        mockMvc.perform(patch("/news/admin/comments/" + commentId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"HIDDEN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("HIDDEN"));

        // 4. Admin exclui comentário
        mockMvc.perform(delete("/news/admin/comments/" + commentId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Admin deve criar, atualizar, duplicar e gerenciar showcase de matérias")
    void testAdminArticleManagementAndShowcase() throws Exception {
        String adminToken = obtainToken("admin@esporteclubepelotas.com.br", "admin123");

        String categoryId = categoryRepository.findAll().get(0).getId().toString();

        CreateNewsDto createDto = CreateNewsDto.builder()
                .title("Novo reforço chega à Boca do Lobo para o Gauchão 2026")
                .subtitle("Atacante experiente assina contrato e já treina com o grupo áureo-cerúleo.")
                .category(categoryId)
                .content(Map.of("type", "doc", "content", List.of(Map.of("type", "paragraph"))))
                .coverImage("https://images.unsplash.com/photo-1574629810360-7efbbe195018")
                .summary("Reforço de peso chega para compor o ataque do Lobo.")
                .tags(List.of("Reforço", "Lobo"))
                .status(NewsStatus.DRAFT)
                .isFeatured(false)
                .featuredSlot(NewsFeaturedSlot.NONE)
                .build();

        // 1. Criar nova matéria
        MvcResult createResult = mockMvc.perform(post("/news/admin")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Novo reforço chega à Boca do Lobo para o Gauchão 2026"))
                .andExpect(jsonPath("$.slug").isNotEmpty())
                .andReturn();

        JsonNode createdNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String articleId = createdNode.get("id").asText();

        // 2. Atualizar matéria
        UpdateNewsDto updateDto = UpdateNewsDto.builder()
                .subtitle("Atualização: Apresentação oficial agendada para amanhã.")
                .build();

        mockMvc.perform(patch("/news/admin/" + articleId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtitle").value("Atualização: Apresentação oficial agendada para amanhã."));

        // 3. Duplicar matéria
        mockMvc.perform(post("/news/admin/" + articleId + "/duplicate")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Novo reforço chega à Boca do Lobo para o Gauchão 2026 (Cópia)"))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        // 4. Consultar vitrine da capa (Showcase)
        mockMvc.perform(get("/news/admin/showcase")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.main").exists())
                .andExpect(jsonPath("$.secondary").isArray())
                .andExpect(jsonPath("$.available").isArray());

        // 5. Excluir matéria criada (soft-delete)
        mockMvc.perform(delete("/news/admin/" + articleId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}

