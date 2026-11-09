package com.esporteclubepelotas.api.modules.anthem.controller;

import com.esporteclubepelotas.api.modules.anthem.dto.ClubAnthemRequest;
import com.esporteclubepelotas.api.modules.anthem.dto.ClubAnthemResponse;
import com.esporteclubepelotas.api.modules.anthem.service.ClubAnthemService;
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
import java.util.UUID;

@RestController
@RequestMapping("/admin/anthem")
@RequiredArgsConstructor
@Tag(name = "Admin - Hino Oficial", description = "Gestão de versões, letras, áudios e histórico do Hino Oficial")
@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
public class ClubAnthemAdminController {

    private final ClubAnthemService anthemService;

    @GetMapping
    @Operation(summary = "Listar todas as versões do hino cadastradas")
    public ResponseEntity<List<ClubAnthemResponse>> listAll() {
        return ResponseEntity.ok(anthemService.listAllForAdmin());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter dados de uma versão específica do hino")
    public ResponseEntity<ClubAnthemResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(anthemService.getAnthemById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova versão do hino")
    public ResponseEntity<ClubAnthemResponse> create(@Valid @RequestBody ClubAnthemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(anthemService.createAnthem(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar versão do hino")
    public ResponseEntity<ClubAnthemResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ClubAnthemRequest request
    ) {
        return ResponseEntity.ok(anthemService.updateAnthem(id, request));
    }

    @PatchMapping("/{id}/official")
    @Operation(summary = "Definir versão do hino como a oficial principal do clube")
    public ResponseEntity<ClubAnthemResponse> setOfficial(@PathVariable UUID id) {
        return ResponseEntity.ok(anthemService.setOfficial(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir versão do hino (Soft delete)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        anthemService.deleteAnthem(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload-audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de arquivo de áudio do hino")
    public ResponseEntity<Map<String, String>> uploadAudio(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "audio", required = false) MultipartFile audio
    ) {
        MultipartFile target = file != null ? file : audio;
        return ResponseEntity.ok(anthemService.uploadAudio(target));
    }

    @PostMapping(value = "/upload-cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de capa/imagem representativa da versão do hino")
    public ResponseEntity<Map<String, String>> uploadCover(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "cover", required = false) MultipartFile cover
    ) {
        MultipartFile target = file != null ? file : cover;
        return ResponseEntity.ok(anthemService.uploadCover(target));
    }
}
