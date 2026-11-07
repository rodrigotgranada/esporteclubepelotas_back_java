package com.esporteclubepelotas.api.modules.football.controller;

import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.football.dto.*;
import com.esporteclubepelotas.api.modules.football.service.SquadsAdminService;
import com.esporteclubepelotas.api.modules.football.service.YouthPortalService;
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
@RequestMapping("/squads/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('football.manage') or hasRole('ADMIN') or hasRole('OWNER') or hasRole('EDITOR')")
@Tag(name = "Futebol: Elencos (Gestão Administrativa)", description = "CRUD de elencos por temporada, montagem, membros e portal da base")
public class SquadsAdminController {

    private final SquadsAdminService squadsAdminService;
    private final YouthPortalService youthPortalService;

    @GetMapping("/youth-config")
    @Operation(summary = "Obter configurações administrativas do Portal da Base")
    public ResponseEntity<YouthPortalConfigDto> getYouthConfig() {
        return ResponseEntity.ok(youthPortalService.getConfig());
    }

    @PutMapping("/youth-config")
    @Operation(summary = "Atualizar pilares, critérios de peneiras e contato do Portal da Base")
    public ResponseEntity<YouthPortalConfigDto> updateYouthConfig(
            @RequestBody @Valid YouthPortalConfigDto dto
    ) {
        return ResponseEntity.ok(youthPortalService.updateConfig(dto));
    }

    @PostMapping(value = "/upload-cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload prévio de capa de elenco antes da persistência no banco")
    public ResponseEntity<Map<String, String>> uploadStandaloneCover(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderId", required = false) String folderId
    ) {
        return ResponseEntity.ok(squadsAdminService.uploadStandaloneCover(file, folderId));
    }

    @GetMapping
    @Operation(summary = "Listar elencos cadastrados com filtros e paginação")
    public ResponseEntity<GetSquadsResponseDto> findAll(@ModelAttribute QuerySquadsDto query) {
        return ResponseEntity.ok(squadsAdminService.findAll(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter elenco por ID com atletas populados")
    public ResponseEntity<SquadDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(squadsAdminService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova temporada/elenco")
    public ResponseEntity<SquadDto> create(
            @RequestBody @Valid CreateSquadDto dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        String userId = principal != null ? principal.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(squadsAdminService.create(dto, userId));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar informações gerais do elenco")
    public ResponseEntity<SquadDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateSquadDto dto
    ) {
        return ResponseEntity.ok(squadsAdminService.update(id, dto));
    }

    @PutMapping("/{id}/members")
    @Operation(summary = "Definir lista completa de membros do elenco em lote")
    public ResponseEntity<SquadDto> setMembers(
            @PathVariable UUID id,
            @RequestBody @Valid SetSquadMembersDto dto
    ) {
        return ResponseEntity.ok(squadsAdminService.setMembers(id, dto));
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Adicionar ou atualizar um membro específico no elenco")
    public ResponseEntity<SquadDto> addOrUpdateMember(
            @PathVariable UUID id,
            @RequestBody @Valid SquadMemberDto memberDto
    ) {
        return ResponseEntity.ok(squadsAdminService.addOrUpdateMember(id, memberDto));
    }

    @DeleteMapping("/{id}/members/{athleteId}")
    @Operation(summary = "Remover atleta do elenco")
    public ResponseEntity<SquadDto> removeMember(
            @PathVariable UUID id,
            @PathVariable UUID athleteId
    ) {
        return ResponseEntity.ok(squadsAdminService.removeMember(id, athleteId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar elenco (Soft Delete)")
    public ResponseEntity<Map<String, Object>> remove(@PathVariable UUID id) {
        squadsAdminService.remove(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Elenco desativado com sucesso."));
    }

    @PostMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Fazer upload da foto oficial de capa do elenco")
    public ResponseEntity<Map<String, String>> uploadCover(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(squadsAdminService.uploadCover(id, file));
    }
}
