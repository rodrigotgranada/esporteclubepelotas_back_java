package com.esporteclubepelotas.api;

import com.esporteclubepelotas.api.common.email.EmailService;
import com.esporteclubepelotas.api.modules.auth.dto.LoginRequestDto;
import com.esporteclubepelotas.api.modules.news.dto.CreateNewsDto;
import com.esporteclubepelotas.api.modules.news.dto.UpdateNewsDto;
import com.esporteclubepelotas.api.modules.news.entity.NewsStatus;
import com.esporteclubepelotas.api.modules.news.repository.NewsArticleRepository;
import com.esporteclubepelotas.api.modules.news.repository.NewsCategoryRepository;
import com.esporteclubepelotas.api.modules.newsletter.dto.SubscribeNewsletterDto;
import com.esporteclubepelotas.api.modules.newsletter.dto.UnsubscribeNewsletterDto;
import com.esporteclubepelotas.api.modules.newsletter.repository.NewsletterSubscriberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class NewsletterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NewsletterSubscriberRepository subscriberRepository;

    @Autowired
    private NewsCategoryRepository categoryRepository;

    @Autowired
    private NewsArticleRepository articleRepository;

    @MockitoSpyBean
    private EmailService emailService;

    private String obtainAdminToken() throws Exception {
        LoginRequestDto loginDto = LoginRequestDto.builder()
                .cpf("admin@esporteclubepelotas.com.br")
                .password("admin123")
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
    @DisplayName("Newsletter deve enviar boas-vindas ao inscrever, notificar inscritos na publicacao sem duplicar e permitir descadastro por token")
    void testSubscribeDispatchAndUnsubscribe() throws Exception {
        clearInvocations(emailService);
        String email = "torcedor.news." + System.currentTimeMillis() + "@pelotas.com.br";

        SubscribeNewsletterDto subscribeDto = SubscribeNewsletterDto.builder()
                .email(email)
                .name("Torcedor Lobão")
                .build();

        mockMvc.perform(post("/newsletter/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscribeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.active").value(true));

        var subscriber = subscriberRepository.findByEmailIgnoreCase(email).orElseThrow();
        assertThat(subscriber.getUnsubscribeToken()).isNotBlank();

        verify(emailService, timeout(4000).times(1))
                .sendHtmlEmail(eq(email), eq("Bem-vindo à Newsletter Oficial - E.C. Pelotas"), contains(subscriber.getUnsubscribeToken()));

        clearInvocations(emailService);

        String adminToken = obtainAdminToken();
        String categoryId = categoryRepository.findAll().get(0).getId().toString();

        CreateNewsDto createDto = CreateNewsDto.builder()
                .title("Lobo vence jogo-treino na Boca do Lobo")
                .subtitle("Preparação segue intensa para a temporada.")
                .summary("Equipe áureo-cerúlea marcou três vezes no teste.")
                .category(categoryId)
                .content(Map.of("type", "doc", "content", java.util.List.of()))
                .status(NewsStatus.PUBLISHED)
                .sendNewsletter(true)
                .build();

        MvcResult newsResult = mockMvc.perform(post("/news/admin")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sendNewsletter").value(true))
                .andExpect(jsonPath("$.newsletterSentAt").isNotEmpty())
                .andReturn();

        String articleId = objectMapper.readTree(newsResult.getResponse().getContentAsString()).get("id").asText();

        verify(emailService, timeout(4000).times(1))
                .sendHtmlEmail(eq(email), eq("📰 E.C. Pelotas: Lobo vence jogo-treino na Boca do Lobo"), contains(subscriber.getUnsubscribeToken()));

        clearInvocations(emailService);

        // Editar a mesma matéria publicada não deve disparar novo e-mail duplicado
        UpdateNewsDto updateDto = UpdateNewsDto.builder()
                .subtitle("Atualização de linha fina sem duplicar envio.")
                .sendNewsletter(true)
                .build();

        mockMvc.perform(patch("/news/admin/" + articleId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        Thread.sleep(300);
        verify(emailService, timeout(500).times(0))
                .sendHtmlEmail(eq(email), contains("Lobo vence jogo-treino"), contains(subscriber.getUnsubscribeToken()));

        // Descadastro via token público
        UnsubscribeNewsletterDto unsubDto = UnsubscribeNewsletterDto.builder()
                .token(subscriber.getUnsubscribeToken())
                .build();

        mockMvc.perform(post("/newsletter/unsubscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unsubDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        var updatedSub = subscriberRepository.findById(subscriber.getId()).orElseThrow();
        assertThat(updatedSub.isActive()).isFalse();

        articleRepository.deleteById(UUID.fromString(articleId));
        subscriberRepository.deleteById(subscriber.getId());
    }
}

