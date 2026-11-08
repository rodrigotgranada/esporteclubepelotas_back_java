package com.esporteclubepelotas.api.modules.setting.controller;

import com.esporteclubepelotas.api.modules.setting.dto.SettingResponseDto;
import com.esporteclubepelotas.api.modules.setting.dto.SettingUpdateDto;
import com.esporteclubepelotas.api.modules.setting.service.SettingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@Tag(name = "Settings", description = "Configurações Globais do Portal (Redes, Contatos, Estádio, Logotipos)")
public class SettingController {

    private final SettingService settingService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Operation(summary = "Obtém as configurações globais do portal (Público)")
    public ResponseEntity<SettingResponseDto> getSettings() {
        return ResponseEntity.ok(settingService.getSettings());
    }

    @GetMapping("/public")
    @Operation(summary = "Alias público para as configurações globais com cache")
    public ResponseEntity<SettingResponseDto> getSettingsPublic() {
        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.maxAge(60, java.util.concurrent.TimeUnit.SECONDS)
                        .cachePublic()
                        .staleWhileRevalidate(120, java.util.concurrent.TimeUnit.SECONDS))
                .body(settingService.getSettings());
    }

    @PostMapping(value = "/upload-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('settings.manage', 'users.manage', 'ROLE_ADMIN', 'ROLE_OWNER')")
    @Operation(summary = "Upload prévio do escudo/logotipo antes da gravação das configurações (Admin)")
    public ResponseEntity<Map<String, String>> uploadStandaloneLogo(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "logo", required = false) MultipartFile logo
    ) {
        MultipartFile target = file != null ? file : logo;
        if (target == null || target.isEmpty()) {
            throw new IllegalArgumentException("O arquivo de logotipo é obrigatório.");
        }
        return ResponseEntity.ok(settingService.uploadStandaloneLogo(target));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('settings.manage', 'users.manage', 'ROLE_ADMIN', 'ROLE_OWNER')")
    @Operation(summary = "Atualiza configurações globais via Multipart (com upload de logo opcional) (Admin)")
    public ResponseEntity<SettingResponseDto> updateSettingsMultipart(
            @RequestParam(value = "data", required = false) String dataStr,
            @RequestPart(value = "logo", required = false) MultipartFile logoFile
    ) {
        SettingUpdateDto dto = null;
        if (dataStr != null && !dataStr.isBlank()) {
            try {
                dto = objectMapper.readValue(dataStr, SettingUpdateDto.class);
            } catch (Exception e) {
                log.error("Erro ao converter payload JSON de configurações: {}", e.getMessage());
                throw new IllegalArgumentException("Payload 'data' não é um JSON válido: " + e.getMessage());
            }
        }
        return ResponseEntity.ok(settingService.updateSettings(dto, logoFile));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyAuthority('settings.manage', 'users.manage', 'ROLE_ADMIN', 'ROLE_OWNER')")
    @Operation(summary = "Atualiza configurações globais diretamente via JSON (Admin)")
    public ResponseEntity<SettingResponseDto> updateSettingsJson(
            @RequestBody SettingUpdateDto dto
    ) {
        return ResponseEntity.ok(settingService.updateSettings(dto, null));
    }
}

