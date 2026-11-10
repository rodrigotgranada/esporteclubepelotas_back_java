package com.esporteclubepelotas.api.modules.match.controller;

import com.esporteclubepelotas.api.modules.match.dto.MatchLiveDetailsResponse;
import com.esporteclubepelotas.api.modules.match.service.MatchLiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matches/public")
@RequiredArgsConstructor
public class MatchesLivePublicController {

    private final MatchLiveService matchLiveService;

    @GetMapping("/{matchIdOrSlug}/live")
    public ResponseEntity<MatchLiveDetailsResponse> getPublicLiveDetails(@PathVariable String matchIdOrSlug) {
        return ResponseEntity.ok(matchLiveService.getLiveDetails(matchIdOrSlug));
    }
}
