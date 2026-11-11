package com.esporteclubepelotas.api.modules.transparency.controller;

import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;
import com.esporteclubepelotas.api.modules.transparency.dto.*;
import com.esporteclubepelotas.api.modules.transparency.service.TransparencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/admin/transparency")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('transparency.manage') or hasRole('ADMIN') or hasRole('OWNER') or hasRole('EDITOR')")
@Tag(name = "Transparência & Governança (Admin)", description = "CRUD de balanços, relatórios, atas e diretoria do clube")
public class TransparencyAdminController {

    private final TransparencyService transparencyService;

    // --- DOCUMENTOS ---

    @GetMapping("/documents")
    @Operation(summary = "Listar todos os documentos cadastrados para gestão")
    public ResponseEntity<List<TransparencyDocumentDto>> listAllDocuments() {
        return ResponseEntity.ok(transparencyService.listAllAdminDocuments());
    }

    @PostMapping("/documents")
    @AuditLog(module = "TRANSPARENCY", action = "CREATE_DOCUMENT", description = "Cadastro de documento de transparência")
    @Operation(summary = "Cadastrar novo documento de transparência ou prestação de contas")
    public ResponseEntity<TransparencyDocumentDto> createDocument(@Valid @RequestBody CreateTransparencyDocumentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transparencyService.createDocument(dto));
    }

    @PutMapping("/documents/{id}")
    @AuditLog(module = "TRANSPARENCY", action = "UPDATE_DOCUMENT", description = "Atualização de documento de transparência")
    @Operation(summary = "Atualizar documento de transparência existente")
    public ResponseEntity<TransparencyDocumentDto> updateDocument(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTransparencyDocumentDto dto
    ) {
        return ResponseEntity.ok(transparencyService.updateDocument(id, dto));
    }

    @DeleteMapping("/documents/{id}")
    @AuditLog(module = "TRANSPARENCY", action = "DELETE_DOCUMENT", description = "Exclusão de documento de transparência")
    @Operation(summary = "Excluir documento de transparência")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        transparencyService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de PDF de relatório ou balanço contábil")
    public ResponseEntity<Map<String, String>> uploadDocumentFile(
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(transparencyService.uploadFile(file));
    }

    // --- CONSELHO & DIRETORIA ---

    @GetMapping("/board-members")
    @Operation(summary = "Listar todos os membros de conselhos cadastrados")
    public ResponseEntity<List<TransparencyBoardMemberDto>> listAllBoardMembers() {
        return ResponseEntity.ok(transparencyService.listAllAdminBoardMembers());
    }

    @PostMapping("/board-members")
    @AuditLog(module = "TRANSPARENCY", action = "CREATE_BOARD_MEMBER", description = "Cadastro de conselheiro/diretor")
    @Operation(summary = "Cadastrar novo membro de conselho ou diretoria")
    public ResponseEntity<TransparencyBoardMemberDto> createBoardMember(@Valid @RequestBody CreateTransparencyBoardMemberDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transparencyService.createBoardMember(dto));
    }

    @PutMapping("/board-members/{id}")
    @AuditLog(module = "TRANSPARENCY", action = "UPDATE_BOARD_MEMBER", description = "Atualização de conselheiro/diretor")
    @Operation(summary = "Atualizar membro de conselho ou diretoria existente")
    public ResponseEntity<TransparencyBoardMemberDto> updateBoardMember(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTransparencyBoardMemberDto dto
    ) {
        return ResponseEntity.ok(transparencyService.updateBoardMember(id, dto));
    }

    @DeleteMapping("/board-members/{id}")
    @AuditLog(module = "TRANSPARENCY", action = "DELETE_BOARD_MEMBER", description = "Exclusão de conselheiro/diretor")
    @Operation(summary = "Excluir membro de conselho ou diretoria")
    public ResponseEntity<Void> deleteBoardMember(@PathVariable UUID id) {
        transparencyService.deleteBoardMember(id);
        return ResponseEntity.noContent().build();
    }
}

