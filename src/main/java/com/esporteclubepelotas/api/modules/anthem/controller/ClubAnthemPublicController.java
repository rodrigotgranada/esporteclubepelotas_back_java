package com.esporteclubepelotas.api.modules.anthem.controller;

import com.esporteclubepelotas.api.modules.anthem.dto.ClubAnthemResponse;
import com.esporteclubepelotas.api.modules.anthem.service.ClubAnthemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/anthem")
@RequiredArgsConstructor
@Tag(name = "Hino Oficial", description = "Endpoints públicos do Hino Oficial do Esporte Clube Pelotas")
public class ClubAnthemPublicController {

    private final ClubAnthemService anthemService;

    @GetMapping
    @Operation(summary = "Obter a versão oficial ativa do Hino do Pelotas")
    public ResponseEntity<ClubAnthemResponse> getOfficialAnthem() {
        ClubAnthemResponse response = anthemService.getOfficialAnthem();
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/versions")
    @Operation(summary = "Listar todas as versões públicas ativas do Hino")
    public ResponseEntity<List<ClubAnthemResponse>> listPublicVersions() {
        return ResponseEntity.ok(anthemService.listPublicVersions());
    }
}
