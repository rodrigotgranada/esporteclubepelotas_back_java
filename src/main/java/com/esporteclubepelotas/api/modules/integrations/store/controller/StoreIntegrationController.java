package com.esporteclubepelotas.api.modules.integrations.store.controller;

import com.esporteclubepelotas.api.modules.auth.dto.LoginRequestDto;
import com.esporteclubepelotas.api.modules.integrations.store.dto.StoreMemberStatusDto;
import com.esporteclubepelotas.api.modules.integrations.store.dto.StorePortalUserDto;
import com.esporteclubepelotas.api.modules.integrations.store.service.StoreIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/integrations/store")
@RequiredArgsConstructor
@Tag(name = "Store Integration (S2S)", description = "Ponte servidor-para-servidor para autenticação e consulta de Sócio pela Loja Oficial Alcateia")
public class StoreIntegrationController {

    private final StoreIntegrationService storeIntegrationService;

    @PostMapping("/authenticate")
    @Operation(summary = "Valida credenciais de usuário do Portal para a Loja (S2S via X-Internal-Api-Key)")
    public ResponseEntity<StorePortalUserDto> authenticateForStore(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @Valid @RequestBody LoginRequestDto request
    ) {
        return ResponseEntity.ok(storeIntegrationService.authenticateForStore(apiKey, request));
    }

    @GetMapping("/member-status")
    @Operation(summary = "Consulta adimplência de Sócio por CPF para a Loja (S2S via X-Internal-Api-Key)")
    public ResponseEntity<StoreMemberStatusDto> getMemberStatus(
            @RequestHeader(value = "X-Internal-Api-Key", required = false) String apiKey,
            @RequestParam("cpf") String cpf
    ) {
        return ResponseEntity.ok(storeIntegrationService.findMemberByCpf(apiKey, cpf));
    }
}
