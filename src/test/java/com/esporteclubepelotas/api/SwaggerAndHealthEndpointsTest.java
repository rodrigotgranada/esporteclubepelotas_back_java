package com.esporteclubepelotas.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SwaggerAndHealthEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Health endpoint deve responder 200 OK e status UP")
    void shouldReturnHealthStatusUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @DisplayName("Raiz deve responder 200 OK e mensagem do Esporte Clube Pelotas")
    void shouldReturnRootApiInfo() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value(containsString("Esporte Clube Pelotas")));
    }

    @Test
    @DisplayName("OpenAPI docs (/v3/api-docs) deve ser público e retornar o título do portal")
    void shouldReturnOpenApiDocs() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Esporte Clube Pelotas")));
    }

    @Test
    @DisplayName("Swagger UI (/swagger-ui/index.html) deve ser acessível sem autenticação")
    void shouldReturnSwaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}

