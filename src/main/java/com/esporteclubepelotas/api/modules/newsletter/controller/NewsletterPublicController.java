package com.esporteclubepelotas.api.modules.newsletter.controller;

import com.esporteclubepelotas.api.modules.newsletter.dto.NewsletterSubscriberDto;
import com.esporteclubepelotas.api.modules.newsletter.dto.SubscribeNewsletterDto;
import com.esporteclubepelotas.api.modules.newsletter.service.NewsletterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/newsletter")
@RequiredArgsConstructor
@Tag(name = "Newsletter (Público)", description = "Inscrição de torcedores para recebimento de novidades oficiais")
public class NewsletterPublicController {

    private final NewsletterService newsletterService;

    @PostMapping("/subscribe")
    @Operation(summary = "Inscrever e-mail na Newsletter oficial do Lobo")
    public ResponseEntity<NewsletterSubscriberDto> subscribe(@Valid @RequestBody SubscribeNewsletterDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsletterService.subscribe(dto));
    }
}

