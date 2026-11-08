package com.esporteclubepelotas.api.modules.membership.controller;

import com.esporteclubepelotas.api.modules.membership.dto.CreateMembershipLeadDto;
import com.esporteclubepelotas.api.modules.membership.dto.MembershipLeadDto;
import com.esporteclubepelotas.api.modules.membership.dto.MembershipPlanDto;
import com.esporteclubepelotas.api.modules.membership.service.MembershipLeadService;
import com.esporteclubepelotas.api.modules.membership.service.MembershipPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/membership-plans")
@RequiredArgsConstructor
@Tag(name = "Planos de Sócios (Público)", description = "Visualização dos planos de sócio ativos e captura de leads para WhatsApp")
public class MembershipPublicController {

    private final MembershipPlanService planService;
    private final MembershipLeadService leadService;

    @GetMapping
    @Operation(summary = "Listar planos de sócios ativos disponíveis para exibição no portal")
    public ResponseEntity<List<MembershipPlanDto>> getActivePlans() {
        return ResponseEntity.ok(planService.findActivePublicPlans());
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Obter detalhes de um plano de sócio pelo slug")
    public ResponseEntity<MembershipPlanDto> getPlanBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(planService.findBySlug(slug));
    }

    @PostMapping("/leads")
    @Operation(summary = "Registrar intenção de adesão (lead) antes do redirecionamento para o WhatsApp da secretaria")
    public ResponseEntity<MembershipLeadDto> createLead(@RequestBody @Valid CreateMembershipLeadDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.createLead(dto));
    }
}
