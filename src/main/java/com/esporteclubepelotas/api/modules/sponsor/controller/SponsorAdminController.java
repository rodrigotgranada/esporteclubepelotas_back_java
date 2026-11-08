package com.esporteclubepelotas.api.modules.sponsor.controller;

import com.esporteclubepelotas.api.common.security.CurrentUser;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;
import com.esporteclubepelotas.api.modules.sponsor.dto.*;
import com.esporteclubepelotas.api.modules.sponsor.service.SponsorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/sponsors")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('sponsors.manage') or hasRole('ADMIN') or hasRole('OWNER') or hasRole('EDITOR')")
@Tag(name = "Patrocinadores (Administração)", description = "Gestão de parceiros, vigências, destaques e métricas de cliques")
public class SponsorAdminController {

    private final SponsorService sponsorService;

    public SponsorAdminController(SponsorService sponsorService) {
        this.sponsorService = sponsorService;
    }

    @GetMapping
    @Operation(summary = "Listar patrocinadores com filtros e paginação para o painel de gestão")
    public ResponseEntity<GetSponsorsResponseDto> findAll(@ModelAttribute QuerySponsorDto query) {
        return ResponseEntity.ok(sponsorService.findAll(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter dados de um patrocinador por ID para edição")
    public ResponseEntity<SponsorDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(sponsorService.findById(id));
    }

    @PostMapping
    @AuditLog(action = "CREATE_SPONSOR", module = "sponsors")
    @Operation(summary = "Cadastrar novo patrocinador oficial")
    public ResponseEntity<SponsorDto> create(
            @RequestBody @Valid CreateSponsorDto dto,
            @CurrentUser UserPrincipal user
    ) {
        String userId = user != null ? user.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(sponsorService.create(dto, userId));
    }

    @PutMapping("/{id}")
    @AuditLog(action = "UPDATE_SPONSOR", module = "sponsors")
    @Operation(summary = "Atualizar dados de um patrocinador existente")
    public ResponseEntity<SponsorDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateSponsorDto dto,
            @CurrentUser UserPrincipal user
    ) {
        String userId = user != null ? user.getId() : null;
        return ResponseEntity.ok(sponsorService.update(id, dto, userId));
    }

    @PatchMapping("/reorder")
    @AuditLog(action = "REORDER_SPONSORS", module = "sponsors")
    @Operation(summary = "Reordenar posições de exibição dos patrocinadores")
    public ResponseEntity<Map<String, Object>> reorder(
            @RequestBody @Valid List<SponsorReorderItemDto> items,
            @CurrentUser UserPrincipal user
    ) {
        String userId = user != null ? user.getId() : null;
        sponsorService.reorder(items, userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Patrocinadores reordenados com sucesso."));
    }

    @DeleteMapping("/{id}")
    @AuditLog(action = "DELETE_SPONSOR", module = "sponsors")
    @Operation(summary = "Inativar patrocinador via soft-delete permanente")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable UUID id,
            @CurrentUser UserPrincipal user
    ) {
        String userId = user != null ? user.getId() : null;
        sponsorService.delete(id, userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Patrocinador excluído com sucesso."));
    }

    @PostMapping(value = "/upload-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload do logotipo do patrocinador com contrato canônico ('file' e alias 'logo')")
    public ResponseEntity<Map<String, String>> uploadLogo(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "logo", required = false) MultipartFile logo
    ) {
        MultipartFile target = file != null ? file : logo;
        return ResponseEntity.ok(sponsorService.uploadLogo(target));
    }
}
