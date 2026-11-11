package com.esporteclubepelotas.api.modules.football.controller;

import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;

import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.football.dto.AthleteDto;
import com.esporteclubepelotas.api.modules.football.dto.CreateAthleteDto;
import com.esporteclubepelotas.api.modules.football.dto.GetAthletesResponseDto;
import com.esporteclubepelotas.api.modules.football.dto.QueryAthletesDto;
import com.esporteclubepelotas.api.modules.football.dto.UpdateAthleteDto;
import com.esporteclubepelotas.api.modules.football.service.AthletesAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/athletes/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('football.manage') or hasRole('ADMIN') or hasRole('OWNER') or hasRole('EDITOR')")
@Tag(name = "Futebol: Atletas (Gestão Administrativa)", description = "CRUD de atletas do banco permanente e uploads")

public class AthletesAdminController {

    private final AthletesAdminService athletesAdminService;

    @GetMapping
    @Operation(summary = "Listar atletas para o painel de administração")
    public ResponseEntity<GetAthletesResponseDto> findAll(@ModelAttribute QueryAthletesDto query) {
        return ResponseEntity.ok(athletesAdminService.findAll(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter atleta por ID para edição")
    public ResponseEntity<AthleteDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(athletesAdminService.findById(id));
    }

    @PostMapping
    @AuditLog(module = "ATHLETES", action = "CREATE_ATHLETE", description = "Cadastro de atleta")
    @Operation(summary = "Cadastrar novo atleta no banco permanente")
    public ResponseEntity<AthleteDto> create(
            @RequestBody @Valid CreateAthleteDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        String userId = principal != null ? principal.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(athletesAdminService.create(dto, userId));
    }

    @PatchMapping("/{id}")
    @AuditLog(module = "ATHLETES", action = "UPDATE_ATHLETE", description = "Atualização de atleta")
    @Operation(summary = "Atualizar dados cadastrais do atleta")
    public ResponseEntity<AthleteDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateAthleteDto dto
    ) {
        return ResponseEntity.ok(athletesAdminService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @AuditLog(module = "ATHLETES", action = "DELETE_ATHLETE", description = "Exclusão de atleta")
    @Operation(summary = "Inativar atleta (Soft Delete)")
    public ResponseEntity<Map<String, Object>> remove(@PathVariable UUID id) {
        athletesAdminService.remove(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Atleta desativado com sucesso."));
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Fazer upload da foto oficial do atleta")
    public ResponseEntity<Map<String, String>> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(athletesAdminService.uploadPhoto(id, file));
    }
}

