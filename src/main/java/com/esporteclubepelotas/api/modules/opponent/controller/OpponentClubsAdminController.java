package com.esporteclubepelotas.api.modules.opponent.controller;

import com.esporteclubepelotas.api.modules.opponent.dto.OpponentClubRequest;
import com.esporteclubepelotas.api.modules.opponent.dto.OpponentClubResponse;
import com.esporteclubepelotas.api.modules.opponent.service.OpponentClubService;
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
@RequestMapping("/opponents/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
public class OpponentClubsAdminController {

    private final OpponentClubService clubService;

    @GetMapping
    public ResponseEntity<List<OpponentClubResponse>> getAll(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(clubService.findAll(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpponentClubResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(clubService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OpponentClubResponse> create(@Valid @RequestBody OpponentClubRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clubService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpponentClubResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody OpponentClubRequest request
    ) {
        return ResponseEntity.ok(clubService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        clubService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadBadge(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "badge", required = false) MultipartFile badge
    ) {
        MultipartFile target = file != null && !file.isEmpty() ? file : badge;
        String url = clubService.uploadBadge(target);
        return ResponseEntity.ok(Map.of("url", url));
    }
}

