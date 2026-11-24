package com.esporteclubepelotas.api;

import com.esporteclubepelotas.api.modules.auth.dto.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StoreIntegrationTest {

    private static final String TEST_S2S_KEY = "test-store-s2s-key-1908";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("S2S /integrations/store/authenticate deve rejeitar chamadas sem ou com chave inválida")
    void shouldRejectInvalidInternalKey() throws Exception {
        LoginRequestDto login = LoginRequestDto.builder()
                .cpf("00000000001")
                .password("admin123")
                .build();

        mockMvc.perform(post("/integrations/store/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/integrations/store/authenticate")
                        .header("X-Internal-Api-Key", "chave-errada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("S2S /integrations/store/authenticate e /member-status devem autenticar e retornar status de Sócio")
    void shouldAuthenticateAndReturnMemberStatusWithValidKey() throws Exception {
        LoginRequestDto login = LoginRequestDto.builder()
                .cpf("00000000001")
                .password("admin123")
                .build();

        mockMvc.perform(post("/integrations/store/authenticate")
                        .header("X-Internal-Api-Key", TEST_S2S_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("00000000001"))
                .andExpect(jsonPath("$.role").value("OWNER"));

        mockMvc.perform(get("/integrations/store/member-status")
                        .header("X-Internal-Api-Key", TEST_S2S_KEY)
                        .param("cpf", "00000000004"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found").value(true));
    }
}
