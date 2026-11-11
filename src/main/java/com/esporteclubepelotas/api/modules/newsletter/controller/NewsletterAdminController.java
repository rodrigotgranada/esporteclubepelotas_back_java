package com.esporteclubepelotas.api.modules.newsletter.controller;

import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;
import com.esporteclubepelotas.api.modules.newsletter.dto.NewsletterSubscriberDto;
import com.esporteclubepelotas.api.modules.newsletter.service.NewsletterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/newsletter")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('newsletter.manage') or hasRole('ADMIN') or hasRole('OWNER') or hasRole('EDITOR')")
@Tag(name = "Newsletter (Admin)", description = "Gestão de inscritos e relatórios da Newsletter Oficial")
public class NewsletterAdminController {

    private final NewsletterService newsletterService;

    @GetMapping("/subscribers")
    @Operation(summary = "Listar torcedores inscritos na newsletter com paginação")
    public ResponseEntity<Page<NewsletterSubscriberDto>> listSubscribers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(newsletterService.listSubscribers(page, limit));
    }

    @GetMapping("/subscribers/all")
    @Operation(summary = "Listar todos os e-mails inscritos (para exportação)")
    public ResponseEntity<List<NewsletterSubscriberDto>> listAllSubscribers() {
        return ResponseEntity.ok(newsletterService.listAllSubscribers());
    }

    @DeleteMapping("/subscribers/{id}")
    @AuditLog(module = "NEWSLETTER", action = "DELETE_SUBSCRIBER", description = "Remoção de torcedor da lista de newsletter")
    @Operation(summary = "Remover torcedor da lista de newsletter")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable UUID id) {
        newsletterService.deleteSubscriber(id);
        return ResponseEntity.noContent().build();
    }
}

