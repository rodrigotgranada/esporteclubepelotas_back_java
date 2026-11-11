package com.esporteclubepelotas.api.modules.transparency.controller;

import com.esporteclubepelotas.api.modules.transparency.dto.TransparencyBoardMemberDto;
import com.esporteclubepelotas.api.modules.transparency.dto.TransparencyDocumentDto;
import com.esporteclubepelotas.api.modules.transparency.service.TransparencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transparency")
@RequiredArgsConstructor
@Tag(name = "Transparência & Governança (Público)", description = "Estatutos, balanços patrimoniais, relatórios de prestação de contas e diretoria")
public class TransparencyPublicController {

    private final TransparencyService transparencyService;

    @GetMapping("/documents")
    @Operation(summary = "Listar documentos oficiais de transparência publicados")
    public ResponseEntity<List<TransparencyDocumentDto>> listDocuments(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(transparencyService.listPublicDocuments(category, year));
    }

    @GetMapping("/board-members")
    @Operation(summary = "Listar composição da diretoria executiva e conselhos deliberativo e fiscal")
    public ResponseEntity<List<TransparencyBoardMemberDto>> listBoardMembers(
            @RequestParam(required = false) String boardType
    ) {
        return ResponseEntity.ok(transparencyService.listPublicBoardMembers(boardType));
    }
}

