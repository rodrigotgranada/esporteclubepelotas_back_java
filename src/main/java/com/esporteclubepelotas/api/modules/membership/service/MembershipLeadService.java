package com.esporteclubepelotas.api.modules.membership.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.membership.dto.CreateMembershipLeadDto;
import com.esporteclubepelotas.api.modules.membership.dto.MembershipLeadDto;
import com.esporteclubepelotas.api.modules.membership.dto.UpdateMembershipLeadStatusDto;
import com.esporteclubepelotas.api.modules.membership.entity.MembershipLead;
import com.esporteclubepelotas.api.modules.membership.entity.MembershipPlan;
import com.esporteclubepelotas.api.modules.membership.helper.MembershipMapperHelper;
import com.esporteclubepelotas.api.modules.membership.repository.MembershipLeadRepository;
import com.esporteclubepelotas.api.modules.membership.repository.MembershipPlanRepository;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipLeadService {

    private final MembershipLeadRepository leadRepository;
    private final MembershipPlanRepository planRepository;
    private final UserRepository userRepository;
    private final PortalWebSocketHandler webSocketHandler;

    @Transactional
    public MembershipLeadDto createLead(CreateMembershipLeadDto dto) {
        MembershipPlan plan = planRepository.findActiveById(dto.getPlanId())
                .orElseThrow(() -> new BusinessException("Plano de sócio selecionado não encontrado ou inativo."));

        User user = null;
        if (dto.getUserId() != null && !dto.getUserId().isBlank()) {
            user = userRepository.findById(dto.getUserId()).orElse(null);
        }

        String sanitizedPhone = dto.getPhone() != null ? dto.getPhone().trim() : "";
        String sanitizedCpf = dto.getCpf() != null ? dto.getCpf().replaceAll("\\D", "") : null;

        MembershipLead lead = MembershipLead.builder()
                .plan(plan)
                .user(user)
                .name(dto.getName().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .phone(sanitizedPhone)
                .cpf(sanitizedCpf)
                .status("NOVO")
                .notes(dto.getNotes())
                .build();

        MembershipLead saved = leadRepository.save(lead);
        log.info("Novo Lead de Adesão registrado: id={}, plano={}, torcedor={}", saved.getId(), plan.getName(), saved.getName());
        webSocketHandler.broadcastAfterCommit("membership_leads", "CREATED", saved.getId().toString());

        return MembershipMapperHelper.toLeadDto(saved);
    }

    @Transactional(readOnly = true)
    public List<MembershipLeadDto> findAll() {
        return leadRepository.findAllWithPlan()
                .stream()
                .map(MembershipMapperHelper::toLeadDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<MembershipLeadDto> findPaginated(String status, int page, int limit) {
        int pageNumber = Math.max(0, page - 1);
        int pageSize = Math.min(100, Math.max(1, limit));
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        String filterStatus = (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL"))
                ? status.trim().toUpperCase()
                : null;

        return leadRepository.findByStatusWithPlan(filterStatus, pageable)
                .map(MembershipMapperHelper::toLeadDto);
    }

    @Transactional
    public MembershipLeadDto updateStatus(UUID id, UpdateMembershipLeadStatusDto dto, String userId) {
        MembershipLead lead = leadRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Lead de sócio não encontrado."));

        lead.setStatus(dto.getStatus().trim().toUpperCase());
        if (dto.getNotes() != null) {
            lead.setNotes(dto.getNotes());
        }

        MembershipLead saved = leadRepository.save(lead);
        log.info("Status do Lead de Adesão atualizado: id={}, status={}, por usuário={}", id, saved.getStatus(), userId);
        webSocketHandler.broadcastAfterCommit("membership_leads", "UPDATED", id.toString());

        return MembershipMapperHelper.toLeadDto(saved);
    }
}
