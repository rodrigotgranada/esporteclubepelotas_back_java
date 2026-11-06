package com.esporteclubepelotas.api.modules.timeline.controller;

import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.timeline.dto.*;
import com.esporteclubepelotas.api.modules.timeline.service.TimelineAdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/timeline/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('timeline.manage') or hasRole('ADMIN') or hasRole('OWNER') or hasRole('EDITOR')")
@Tag(name = "Linha do Tempo (Gestão Administrativa)", description = "CRUD de marcos históricos do Pelotas com upload de imagens")
public class TimelineAdminController {

    private final TimelineAdminService timelineAdminService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Operation(summary = "Listar marcos históricos para o painel de administração com filtros")
    public ResponseEntity<GetTimelineResponseDto> findAll(@ModelAttribute QueryTimelineDto query) {
        return ResponseEntity.ok(timelineAdminService.findAll(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes de um marco histórico por ID")
    public ResponseEntity<TimelineEventDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(timelineAdminService.findById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Criar novo marco histórico com foto opcional (Multipart)")
    public ResponseEntity<TimelineEventDto> createMultipart(
            @RequestParam("data") String dataJson,
            @RequestPart(value = "photo", required = false) MultipartFile photoFile,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        try {
            CreateTimelineEventDto dto = objectMapper.readValue(dataJson, CreateTimelineEventDto.class);
            String userId = principal != null ? principal.getId() : null;
            return ResponseEntity.status(HttpStatus.CREATED).body(timelineAdminService.create(dto, photoFile, userId));
        } catch (Exception e) {
            log.error("Erro ao processar JSON de criação do marco histórico: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Payload 'data' inválido: " + e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Criar novo marco histórico sem upload direto de arquivo (JSON puro)")
    public ResponseEntity<TimelineEventDto> createJson(
            @RequestBody @Valid CreateTimelineEventDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        String userId = principal != null ? principal.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(timelineAdminService.create(dto, null, userId));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Atualizar marco histórico existente com foto opcional (Multipart)")
    public ResponseEntity<TimelineEventDto> updateMultipart(
            @PathVariable UUID id,
            @RequestParam(value = "data", required = false) String dataJson,
            @RequestPart(value = "photo", required = false) MultipartFile photoFile
    ) {
        UpdateTimelineEventDto dto = null;
        if (dataJson != null && !dataJson.isBlank()) {
            try {
                dto = objectMapper.readValue(dataJson, UpdateTimelineEventDto.class);
            } catch (Exception e) {
                log.error("Erro ao converter payload JSON de atualização do marco: {}", e.getMessage(), e);
                throw new IllegalArgumentException("Payload 'data' inválido: " + e.getMessage());
            }
        }
        return ResponseEntity.ok(timelineAdminService.update(id, dto, photoFile));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Atualizar marco histórico existente diretamente via JSON")
    public ResponseEntity<TimelineEventDto> updateJson(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateTimelineEventDto dto
    ) {
        return ResponseEntity.ok(timelineAdminService.update(id, dto, null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar marco histórico (Soft delete)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        timelineAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
