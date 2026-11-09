package com.esporteclubepelotas.api.modules.kit.controller;

import com.esporteclubepelotas.api.modules.kit.dto.ClubKitCompetitionItemResponse;
import com.esporteclubepelotas.api.modules.kit.dto.ClubKitResponse;
import com.esporteclubepelotas.api.modules.kit.service.ClubKitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/kits/public")
@RequiredArgsConstructor
public class ClubKitPublicController {

    private final ClubKitService kitService;

    @GetMapping
    public ResponseEntity<List<ClubKitResponse>> listAll() {
        return ResponseEntity.ok(kitService.listAllForPublic());
    }

    @GetMapping("/current")
    public ResponseEntity<List<ClubKitResponse>> listCurrent() {
        return ResponseEntity.ok(kitService.listCurrentKits());
    }

    @GetMapping("/competitions")
    public ResponseEntity<List<ClubKitCompetitionItemResponse>> listHistoricalCompetitions() {
        return ResponseEntity.ok(kitService.listHistoricalCompetitions());
    }

    @GetMapping("/historical")
    public ResponseEntity<List<ClubKitResponse>> listHistorical(
            @RequestParam(value = "competition", required = false) String competition
    ) {
        return ResponseEntity.ok(kitService.listHistoricalByCompetition(competition));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubKitResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(kitService.getById(id));
    }
}

