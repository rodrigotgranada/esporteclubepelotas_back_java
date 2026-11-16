package com.esporteclubepelotas.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlobalSearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /search com menos de 2 caracteres deve retornar resposta vazia com 200")
    void shouldReturnEmptyForShortQuery() throws Exception {
        mockMvc.perform(get("/search").param("q", "a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults").value(0))
                .andExpect(jsonPath("$.news").isArray())
                .andExpect(jsonPath("$.athletes").isArray())
                .andExpect(jsonPath("$.idols").isArray());
    }

    @Test
    @DisplayName("GET /search deve buscar publicamente em múltiplas entidades com segurança")
    void shouldSearchPubliclyAcrossEntities() throws Exception {
        mockMvc.perform(get("/search").param("q", "Pelotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query").value("Pelotas"))
                .andExpect(jsonPath("$.totalResults", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.news").isArray())
                .andExpect(jsonPath("$.athletes").isArray())
                .andExpect(jsonPath("$.idols").isArray())
                .andExpect(jsonPath("$.matches").isArray())
                .andExpect(jsonPath("$.transparency").isArray());
    }
}

