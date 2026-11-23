package com.esporteclubepelotas.api;

import com.esporteclubepelotas.api.common.security.JwtTokenProvider;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.setting.dto.SettingUpdateDto;
import com.esporteclubepelotas.api.modules.theme.dto.ThemeRequestDto;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ThemeAndSettingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    private String adminToken;

    @BeforeEach
    void setUp() {
        User admin = userRepository.findByEmail("admin@esporteclubepelotas.com.br")
                .orElseThrow(() -> new IllegalStateException("Admin seed não encontrado nos testes."));

        UserPrincipal adminPrincipal = UserPrincipal.builder()
                .id(admin.getId())
                .name(admin.getFirstName() + " " + admin.getLastName())
                .email(admin.getEmail())
                .cpf(admin.getCpf())
                .role("OWNER")
                .permissions(List.of("*", "settings.manage", "settings.view"))
                .active(true)
                .tokenVersion(admin.getTokenVersion())
                .build();

        adminToken = "Bearer " + jwtTokenProvider.generateAccessToken(adminPrincipal);
    }

    @Test
    @DisplayName("GET /themes/active deve ser público e retornar o tema padrão do clube")
    void shouldReturnActiveThemePublicly() throws Exception {
        mockMvc.perform(get("/themes/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Padrão E.C. Pelotas"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.light.primary").value("#fbbf24"))
                .andExpect(jsonPath("$.light.secondary").value("#1e3a8a"))
                .andExpect(jsonPath("$._id").isNotEmpty());
    }

    @Test
    @DisplayName("GET /settings deve ser público e retornar informações institucionais")
    void shouldReturnSettingsPublicly() throws Exception {
        mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clubName").value(containsString("Pelotas")))
                .andExpect(jsonPath("$.stadiumName").value(containsString("Boca do Lobo")))
                .andExpect(jsonPath("$.socialLinks", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$._id").isNotEmpty());
    }

    @Test
    @DisplayName("GET /themes sem token deve ser barrado com 401")
    void shouldBlockUnauthenticatedGetThemes() throws Exception {
        mockMvc.perform(get("/themes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /themes com token Admin deve retornar a lista de temas")
    void shouldAllowAdminGetThemes() throws Exception {
        mockMvc.perform(get("/themes")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("POST /themes deve criar campanha sazonal com paletas automáticas")
    void shouldCreateCampaignTheme() throws Exception {
        ThemeRequestDto request = ThemeRequestDto.builder()
                .name("Novembro Azul 2026")
                .isCampaign(true)
                .campaignColor("#0284c7")
                .isActive(false)
                .build();

        mockMvc.perform(post("/themes")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Novembro Azul 2026"))
                .andExpect(jsonPath("$.isCampaign").value(true))
                .andExpect(jsonPath("$.campaignColor").value("#0284c7"))
                .andExpect(jsonPath("$.light.primary").value("#0284c7"))
                .andExpect(jsonPath("$.dark.primary").isNotEmpty());
    }

    @Test
    @DisplayName("PUT /settings com token Admin deve atualizar configurações com sucesso")
    void shouldUpdateSettingsJson() throws Exception {
        SettingUpdateDto update = SettingUpdateDto.builder()
                .clubName("Esporte Clube Pelotas - Centenário")
                .stadiumName("Estádio Boca do Lobo (Pelotas)")
                .build();

        mockMvc.perform(put("/settings")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clubName").value("Esporte Clube Pelotas - Centenário"))
                .andExpect(jsonPath("$.stadiumName").value("Estádio Boca do Lobo (Pelotas)"));
    }

    @Test
    @DisplayName("Apenas OWNER deve conseguir configurar créditos de desenvolvimento no rodapé")
    void shouldAllowOnlyOwnerToUpdateDeveloperCredits() throws Exception {
        SettingUpdateDto ownerUpdate = SettingUpdateDto.builder()
                .developerCreditEnabled(true)
                .developerCreditName("Rodrigo Granada")
                .developerCreditUrl("https://rodrigogranada.dev")
                .build();

        mockMvc.perform(put("/settings")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.developerCreditEnabled").value(true))
                .andExpect(jsonPath("$.developerCreditName").value("Rodrigo Granada"))
                .andExpect(jsonPath("$.developerCreditUrl").value("https://rodrigogranada.dev"));

        User adminUser = userRepository.findByEmail("admin@esporteclubepelotas.com.br")
                .orElseThrow();
        UserPrincipal nonOwnerAdmin = UserPrincipal.builder()
                .id(adminUser.getId())
                .name("Administrador Teste")
                .email(adminUser.getEmail())
                .cpf(adminUser.getCpf())
                .role("ADMIN")
                .permissions(List.of("settings.manage", "settings.view"))
                .active(true)
                .tokenVersion(adminUser.getTokenVersion())
                .build();
        String nonOwnerToken = "Bearer " + jwtTokenProvider.generateAccessToken(nonOwnerAdmin);

        SettingUpdateDto adminAttempt = SettingUpdateDto.builder()
                .developerCreditEnabled(false)
                .build();

        mockMvc.perform(put("/settings")
                        .header("Authorization", nonOwnerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminAttempt)))
                .andExpect(status().isForbidden());

        SettingUpdateDto invalidUrlAttempt = SettingUpdateDto.builder()
                .developerCreditEnabled(true)
                .developerCreditName("Rodrigo Granada")
                .developerCreditUrl("javascript:alert(1)")
                .build();

        mockMvc.perform(put("/settings")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUrlAttempt)))
                .andExpect(status().isBadRequest());
    }
}
