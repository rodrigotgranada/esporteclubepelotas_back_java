package com.esporteclubepelotas.api.modules.poll.controller;

import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;
import com.esporteclubepelotas.api.modules.poll.dto.CreatePollDto;
import com.esporteclubepelotas.api.modules.poll.dto.PollDto;
import com.esporteclubepelotas.api.modules.poll.dto.UpdatePollDto;
import com.esporteclubepelotas.api.modules.poll.service.PollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/polls")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('polls.manage') or hasRole('ADMIN') or hasRole('OWNER') or hasRole('EDITOR')")
@Tag(name = "Enquetes Oficiais (Admin)", description = "CRUD de enquetes, opções, ativação e apuração")
public class PollAdminController {

    private final PollService pollService;

    @GetMapping
    @Operation(summary = "Listar todas as enquetes cadastradas")
    public ResponseEntity<List<PollDto>> listAll() {
        return ResponseEntity.ok(pollService.listAllAdmin());
    }

    @PostMapping
    @AuditLog(module = "POLLS", action = "CREATE_POLL", description = "Criação de nova enquete oficial")
    @Operation(summary = "Criar nova enquete com opções de resposta")
    public ResponseEntity<PollDto> create(@Valid @RequestBody CreatePollDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pollService.create(dto));
    }

    @PutMapping("/{id}")
    @AuditLog(module = "POLLS", action = "UPDATE_POLL", description = "Atualização de enquete oficial")
    @Operation(summary = "Atualizar informações de uma enquete")
    public ResponseEntity<PollDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePollDto dto
    ) {
        return ResponseEntity.ok(pollService.update(id, dto));
    }

    @PatchMapping("/{id}/toggle-active")
    @AuditLog(module = "POLLS", action = "TOGGLE_POLL_STATUS", description = "Alternar status ativo/encerrado da enquete")
    @Operation(summary = "Ativar ou encerrar enquete")
    public ResponseEntity<PollDto> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(pollService.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    @AuditLog(module = "POLLS", action = "DELETE_POLL", description = "Exclusão de enquete oficial")
    @Operation(summary = "Excluir enquete e votos associados")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pollService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

