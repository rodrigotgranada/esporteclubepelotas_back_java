package com.esporteclubepelotas.api.modules.opponent.controller;

import com.esporteclubepelotas.api.modules.opponent.dto.OpponentClubResponse;
import com.esporteclubepelotas.api.modules.opponent.service.OpponentClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/opponents/public")
@RequiredArgsConstructor
public class OpponentClubsPublicController {

    private final OpponentClubService clubService;

    @GetMapping
    public ResponseEntity<List<OpponentClubResponse>> getAll(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(clubService.findAll(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpponentClubResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(clubService.findById(id));
    }
}

