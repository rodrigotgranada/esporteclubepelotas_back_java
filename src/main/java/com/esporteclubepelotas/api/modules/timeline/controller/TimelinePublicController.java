package com.esporteclubepelotas.api.modules.timeline.controller;

import com.esporteclubepelotas.api.modules.timeline.dto.GetTimelineResponseDto;
import com.esporteclubepelotas.api.modules.timeline.service.TimelinePublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timeline")
@RequiredArgsConstructor
@Tag(name = "Linha do Tempo (Público)", description = "História oficial e marcos temporais do Esporte Clube Pelotas")
public class TimelinePublicController {

    private final TimelinePublicService timelinePublicService;

    @GetMapping
    @Operation(summary = "Obter todos os marcos da linha do tempo ordenados cronologicamente")
    public ResponseEntity<GetTimelineResponseDto> getTimeline() {
        return ResponseEntity.ok(timelinePublicService.getPublicTimeline());
    }
}
