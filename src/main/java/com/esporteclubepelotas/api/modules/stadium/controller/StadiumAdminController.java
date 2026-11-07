package com.esporteclubepelotas.api.modules.stadium.controller;

import com.esporteclubepelotas.api.modules.stadium.dto.StadiumResponseDto;
import com.esporteclubepelotas.api.modules.stadium.dto.UpdateStadiumDto;
import com.esporteclubepelotas.api.modules.stadium.service.StadiumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/admin/stadium")
@RequiredArgsConstructor
@Tag(name = "Admin - Estádio Boca do Lobo", description = "Gestão de dados, memorial e acervo fotográfico da Boca do Lobo")
@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
public class StadiumAdminController {

    private final StadiumService stadiumService;

    @GetMapping
    @Operation(summary = "Obter dados do Estádio para edição administrativa")
    public ResponseEntity<StadiumResponseDto> getStadiumForAdmin() {
        return ResponseEntity.ok(stadiumService.getStadiumInfo());
    }

    @PutMapping
    @Operation(summary = "Atualizar informações, memorial e galeria do Estádio")
    public ResponseEntity<StadiumResponseDto> updateStadium(@Valid @RequestBody UpdateStadiumDto dto) {
        return ResponseEntity.ok(stadiumService.updateStadiumInfo(dto));
    }

    @PostMapping(value = "/upload-cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de capa principal do Estádio Boca do Lobo")
    public ResponseEntity<Map<String, String>> uploadCover(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(stadiumService.uploadCover(file));
    }

    @PostMapping(value = "/upload-gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de imagem para a galeria histórica do Estádio")
    public ResponseEntity<Map<String, String>> uploadGalleryImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(stadiumService.uploadGalleryImage(file));
    }
}

