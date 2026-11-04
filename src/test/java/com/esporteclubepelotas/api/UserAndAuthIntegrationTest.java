package com.esporteclubepelotas.api;

import com.esporteclubepelotas.api.modules.auth.dto.LoginRequestDto;
import com.esporteclubepelotas.api.modules.auth.dto.ResetPasswordRequestDto;
import com.esporteclubepelotas.api.modules.auth.dto.UserSummaryDto;
import com.esporteclubepelotas.api.modules.user.dto.AdminUpdateUserDto;
import com.esporteclubepelotas.api.modules.user.dto.UpdateMeDto;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserAndAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

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
    @DisplayName("Deve realizar login com sucesso via CPF e retornar JWT com Role OWNER")
    void testLoginWithCpf() throws Exception {
        String token = obtainToken("00000000001", "admin123");
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("Deve verificar disponibilidade de e-mail e CPF")
    void testCheckAvailability() throws Exception {
        mockMvc.perform(get("/users/check-availability?email=admin@esporteclubepelotas.com.br"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));

        mockMvc.perform(get("/users/check-availability?email=novo_disponivel@teste.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("Owner deve listar usuários com filtros e paginação no formato esperado pelo Frontend")
    void testListUsersWithFilters() throws Exception {
        String ownerToken = obtainToken("admin@esporteclubepelotas.com.br", "admin123");

        mockMvc.perform(get("/users?page=1&limit=10&status=ACTIVE")
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.limit").value(10));
    }

    @Test
    @DisplayName("Usuário autenticado deve atualizar o próprio perfil via PATCH /users/me")
    void testUpdateMe() throws Exception {
        String userToken = obtainToken("torcedor@esporteclubepelotas.com.br", "admin123");

        UpdateMeDto updateDto = UpdateMeDto.builder()
                .firstName("Torcedor Fiel")
                .lastName("Lobo da Boca")
                .build();

        mockMvc.perform(patch("/users/me")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Torcedor Fiel"))
                .andExpect(jsonPath("$.lastName").value("Lobo da Boca"));
    }

    @Test
    @DisplayName("Admin NÃO deve conseguir excluir nem promover ninguém a OWNER (Hierarquia)")
    void testHierarchyProtection() throws Exception {
        String adminToken = obtainToken("gestor@esporteclubepelotas.com.br", "admin123");

        // Busca o ID do Owner
        User owner = userRepository.findByEmail("admin@esporteclubepelotas.com.br").orElseThrow();

        // Admin tentando excluir Owner -> Deve retornar 400 Bad Request
        mockMvc.perform(delete("/users/admin/" + owner.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("não têm permissão para excluir")));
    }

    @Test
    @DisplayName("Owner deve conseguir atualizar status e role de outro usuário via endpoints do admin")
    void testAdminUpdateRoleAndStatus() throws Exception {
        String ownerToken = obtainToken("admin@esporteclubepelotas.com.br", "admin123");
        User targetUser = userRepository.findByEmail("torcedor@esporteclubepelotas.com.br").orElseThrow();

        // 1. Atualizar cargo para SÓCIO
        mockMvc.perform(patch("/users/admin/" + targetUser.getId() + "/role")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("role", "SOCIO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role.name").value("SOCIO"));

        // 2. Atualizar status para BLOCKED
        mockMvc.perform(patch("/users/admin/" + targetUser.getId() + "/status")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "BLOCKED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));

        // 3. Reativar status para ACTIVE
        mockMvc.perform(patch("/users/admin/" + targetUser.getId() + "/status")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "ACTIVE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Deve permitir recuperar senha via token e redefinir com sucesso")
    void testResetPasswordFlow() throws Exception {
        User user = userRepository.findByEmail("editor@esporteclubepelotas.com.br").orElseThrow();

        // Dispara forgot password
        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", user.getEmail()))))
                .andExpect(status().isOk());

        // Recupera o token gerado no banco
        User refreshed = userRepository.findById(user.getId()).orElseThrow();
        String resetToken = refreshed.getResetPasswordToken();
        assertThat(resetToken).isNotBlank();

        // Redefine a nova senha
        ResetPasswordRequestDto resetDto = ResetPasswordRequestDto.builder()
                .token(resetToken)
                .newPassword("novaSenha456")
                .build();

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetDto)))
                .andExpect(status().isOk());

        // Testa login com a nova senha
        String newToken = obtainToken(user.getEmail(), "novaSenha456");
        assertThat(newToken).isNotBlank();
    }

    @Test
    @DisplayName("Deve responder rotas de cargos e feature flags no padrão /admin/roles e /admin/modules")
    void testAdminRolesAndModulesEndpoints() throws Exception {
        String ownerToken = obtainToken("admin@esporteclubepelotas.com.br", "admin123");

        mockMvc.perform(get("/admin/roles")
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/admin/modules")
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

