package com.esporteclubepelotas.api.modules.match.controller;

import com.esporteclubepelotas.api.modules.match.dto.*;
import com.esporteclubepelotas.api.modules.match.service.MatchLiveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/matches/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'EDITOR')")
public class MatchesLiveAdminController {

    private final MatchLiveService matchLiveService;

    @GetMapping("/{matchId}/live")
    public ResponseEntity<MatchLiveDetailsResponse> getAdminLiveDetails(@PathVariable UUID matchId) {
        return ResponseEntity.ok(matchLiveService.getLiveDetails(matchId.toString()));
    }

    @PostMapping("/{matchId}/events")
    public ResponseEntity<MatchEventResponse> addEvent(
            @PathVariable UUID matchId,
            @Valid @RequestBody MatchEventRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchLiveService.addEvent(matchId, request));
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID eventId) {
        matchLiveService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{matchId}/live/status")
    public ResponseEntity<MatchLiveDetailsResponse> handleMatchAction(
            @PathVariable UUID matchId,
            @Valid @RequestBody MatchLiveActionRequest request
    ) {
        return ResponseEntity.ok(matchLiveService.handleMatchAction(matchId, request));
    }
}
