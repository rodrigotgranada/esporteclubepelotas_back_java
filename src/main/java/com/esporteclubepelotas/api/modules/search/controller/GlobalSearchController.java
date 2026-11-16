package com.esporteclubepelotas.api.modules.search.controller;

import com.esporteclubepelotas.api.modules.search.dto.GlobalSearchResponseDto;
import com.esporteclubepelotas.api.modules.search.service.GlobalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class GlobalSearchController {

    private final GlobalSearchService globalSearchService;

    @GetMapping
    public ResponseEntity<GlobalSearchResponseDto> search(
            @RequestParam(name = "q", defaultValue = "") String q,
            @RequestParam(name = "limit", defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(globalSearchService.search(q, limit));
    }
}

