package com.esporteclubepelotas.api.modules.integrations.store.controller;

import com.esporteclubepelotas.api.common.security.CurrentUser;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.integrations.store.dto.StoreOrderBridgeDto.CancelStoreOrderRequestDto;
import com.esporteclubepelotas.api.modules.integrations.store.dto.StoreOrderBridgeDto.PortalStoreOrderSummaryDto;
import com.esporteclubepelotas.api.modules.integrations.store.dto.StoreOrderBridgeDto.PortalStoreOrdersResponseDto;
import com.esporteclubepelotas.api.modules.integrations.store.dto.StoreOrderBridgeDto.StoreSsoTokenResponseDto;
import com.esporteclubepelotas.api.modules.integrations.store.service.StoreOrderBridgeService;
import com.esporteclubepelotas.api.modules.integrations.store.service.StoreSsoTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Portal Store User Bridge", description = "SSO Handoff e consulta resiliente de pedidos da Loja no Perfil do Torcedor")
public class StorePortalUserBridgeController {

    private final StoreSsoTokenService storeSsoTokenService;
    private final StoreOrderBridgeService storeOrderBridgeService;

    @PostMapping("/auth/store-sso-token")
    @Operation(summary = "Emite token SSO descartável (60s) para abrir a Loja já autenticado")
    public ResponseEntity<StoreSsoTokenResponseDto> issueStoreSsoToken(@CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(storeSsoTokenService.issueSsoToken(principal.getId()));
    }

    @GetMapping("/users/me/store-orders")
    @Operation(summary = "Consulta pedidos da Loja do usuário logado com fallback gracioso quando Loja estiver offline")
    public ResponseEntity<PortalStoreOrdersResponseDto> listMyStoreOrders(@CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(storeOrderBridgeService.listUserStoreOrders(principal.getId()));
    }

    @PostMapping("/users/me/store-orders/{orderCode}/cancel")
    @Operation(summary = "Solicita cancelamento de reserva em aberto na Loja")
    public ResponseEntity<PortalStoreOrderSummaryDto> cancelMyStoreOrder(
            @CurrentUser UserPrincipal principal,
            @PathVariable String orderCode,
            @RequestBody(required = false) CancelStoreOrderRequestDto body
    ) {
        String reason = body != null ? body.getReason() : null;
        return ResponseEntity.ok(storeOrderBridgeService.cancelUserStoreOrder(principal.getId(), orderCode, reason));
    }
}
