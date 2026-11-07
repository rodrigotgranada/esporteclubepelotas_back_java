package com.esporteclubepelotas.api.modules.stadium.controller;

import com.esporteclubepelotas.api.modules.stadium.dto.StadiumResponseDto;
import com.esporteclubepelotas.api.modules.stadium.service.StadiumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/stadium")
@RequiredArgsConstructor
@Tag(name = "Estádio Boca do Lobo", description = "Memorial, história e ficha técnica do estádio centenário")
public class StadiumController {

    private final StadiumService stadiumService;

    @GetMapping
    @Operation(summary = "Obter dados públicos e história do Estádio Boca do Lobo")
    public ResponseEntity<StadiumResponseDto> getStadium() {
        StadiumResponseDto response = stadiumService.getStadiumInfo();
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS).staleWhileRevalidate(120, TimeUnit.SECONDS).cachePublic())
                .body(response);
    }
}

