package com.esporteclubepelotas.api.modules.idol.controller;

import com.esporteclubepelotas.api.common.security.CurrentUser;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.idol.dto.CreateAdminIdolDto;
import com.esporteclubepelotas.api.modules.idol.dto.GetIdolsResponseDto;
import com.esporteclubepelotas.api.modules.idol.dto.IdolDto;
import com.esporteclubepelotas.api.modules.idol.dto.UpdateAdminIdolDto;
import com.esporteclubepelotas.api.modules.idol.entity.IdolCategory;
import com.esporteclubepelotas.api.modules.idol.service.IdolAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/idols/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('EDITOR')")
@Tag(name = "Ídolos (Gestão Administrativa)", description = "CRUD de Ídolos, Fotos e Galerias Históricas")
public class IdolAdminController {

    private final IdolAdminService idolAdminService;

    @GetMapping
    @Operation(summary = "Listar ídolos para o painel administrativo")
    public ResponseEntity<GetIdolsResponseDto> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) IdolCategory category,
            @RequestParam(required = false) Boolean isFeatured) {
        return ResponseEntity.ok(idolAdminService.findAll(page, limit, search, category, isFeatured));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes de um ídolo para edição por ID")
    public ResponseEntity<IdolDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(idolAdminService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo ídolo no Memorial")
    public ResponseEntity<IdolDto> create(
            @Valid @RequestBody CreateAdminIdolDto dto,
            @CurrentUser UserPrincipal user) {
        String userId = user != null ? user.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(idolAdminService.create(dto, userId));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar dados de um ídolo existente")
    public ResponseEntity<IdolDto> update(
            @PathVariable String id,
            @RequestBody UpdateAdminIdolDto dto,
            @CurrentUser UserPrincipal user) {
        String userId = user != null ? user.getId() : null;
        return ResponseEntity.ok(idolAdminService.update(id, dto, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclusão lógica de um ídolo (soft delete)")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        return ResponseEntity.ok(idolAdminService.softDelete(id));
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload da foto principal do ídolo")
    public ResponseEntity<Map<String, String>> uploadPhoto(
            @PathVariable String id,
            @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "photo", required = false) MultipartFile photo) {
        MultipartFile target = file != null ? file : photo; return ResponseEntity.ok(idolAdminService.uploadPhoto(id, target));
    }

    @PostMapping(value = "/{id}/gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de fotos para a galeria histórica do ídolo")
    public ResponseEntity<Map<String, Object>> uploadGallery(
            @PathVariable String id,
            @RequestParam(value = "files", required = false) List<MultipartFile> files, @RequestParam(value = "gallery", required = false) List<MultipartFile> gallery) {
        List<MultipartFile> target = files != null && !files.isEmpty() ? files : gallery; return ResponseEntity.ok(idolAdminService.uploadGallery(id, target));
    }
}

