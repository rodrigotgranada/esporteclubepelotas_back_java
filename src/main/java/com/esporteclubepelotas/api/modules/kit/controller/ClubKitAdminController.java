package com.esporteclubepelotas.api.modules.kit.controller;

import com.esporteclubepelotas.api.modules.kit.dto.ClubKitRequest;
import com.esporteclubepelotas.api.modules.kit.dto.ClubKitResponse;
import com.esporteclubepelotas.api.modules.kit.service.ClubKitService;
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
@RequestMapping("/kits/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
public class ClubKitAdminController {

    private final ClubKitService kitService;

    @GetMapping
    public ResponseEntity<List<ClubKitResponse>> listAll() {
        return ResponseEntity.ok(kitService.listAllForAdmin());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubKitResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(kitService.getById(id));
    }

    @GetMapping("/competitions")
    public ResponseEntity<List<String>> listDistinctCompetitions() {
        return ResponseEntity.ok(kitService.listAllDistinctCompetitions());
    }

    @PostMapping
    public ResponseEntity<ClubKitResponse> create(@Valid @RequestBody ClubKitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(kitService.createKit(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClubKitResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ClubKitRequest request
    ) {
        return ResponseEntity.ok(kitService.updateKit(id, request));
    }

    @PatchMapping("/{id}/toggle-current")
    public ResponseEntity<ClubKitResponse> toggleCurrent(@PathVariable UUID id) {
        return ResponseEntity.ok(kitService.toggleCurrent(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        kitService.deleteKit(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(kitService.uploadKitImage(file));
    }
}

