package com.esporteclubepelotas.api.modules.poll.controller;

import com.esporteclubepelotas.api.common.security.CurrentUser;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.poll.dto.PollDto;
import com.esporteclubepelotas.api.modules.poll.dto.VotePollDto;
import com.esporteclubepelotas.api.modules.poll.service.PollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/polls")
@RequiredArgsConstructor
@Tag(name = "Enquetes Oficiais (Público / Sócios)", description = "Votações e consultas oficiais da torcida e do quadro social")
public class PollPublicController {

    private final PollService pollService;

    @GetMapping("/active")
    @Operation(summary = "Obter a enquete oficial atualmente ativa no portal")
    public ResponseEntity<PollDto> getActivePoll(@CurrentUser UserPrincipal principal) {
        return pollService.getActivePoll(principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter dados e apuração parcial de uma enquete por ID")
    public ResponseEntity<PollDto> getById(
            @PathVariable UUID id,
            @CurrentUser UserPrincipal principal
    ) {
        return ResponseEntity.ok(pollService.getPollById(id, principal));
    }

    @PostMapping("/{id}/vote")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Registrar voto na enquete (Exclusivo para Sócios Adimplentes)")
    public ResponseEntity<PollDto> vote(
            @PathVariable UUID id,
            @Valid @RequestBody VotePollDto dto,
            @CurrentUser UserPrincipal principal
    ) {
        return ResponseEntity.ok(pollService.vote(id, dto, principal));
    }
}

