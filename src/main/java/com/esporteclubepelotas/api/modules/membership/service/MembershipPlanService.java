package com.esporteclubepelotas.api.modules.membership.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.membership.dto.CreateMembershipPlanDto;
import com.esporteclubepelotas.api.modules.membership.dto.MembershipPlanDto;
import com.esporteclubepelotas.api.modules.membership.dto.UpdateMembershipPlanDto;
import com.esporteclubepelotas.api.modules.membership.entity.MembershipPlan;
import com.esporteclubepelotas.api.modules.membership.helper.MembershipMapperHelper;
import com.esporteclubepelotas.api.modules.membership.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipPlanService {

    private final MembershipPlanRepository planRepository;
    private final PortalWebSocketHandler webSocketHandler;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Transactional(readOnly = true)
    @Cacheable(value = "membership_plans_public", sync = true)
    public List<MembershipPlanDto> findActivePublicPlans() {
        return planRepository.findAllActivePlans()
                .stream()
                .map(MembershipMapperHelper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MembershipPlanDto> findAdminPlans() {
        return planRepository.findAllAdminPlans()
                .stream()
                .map(MembershipMapperHelper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MembershipPlanDto findById(UUID id) {
        MembershipPlan plan = planRepository.findActiveById(id)
                .orElseThrow(() -> new BusinessException("Plano de sócio não encontrado."));
        return MembershipMapperHelper.toDto(plan);
    }

    @Transactional(readOnly = true)
    public MembershipPlanDto findBySlug(String slug) {
        MembershipPlan plan = planRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException("Plano de sócio não encontrado pelo identificador fornecido."));
        return MembershipMapperHelper.toDto(plan);
    }

    @Transactional
    @CacheEvict(value = "membership_plans_public", allEntries = true)
    public MembershipPlanDto create(CreateMembershipPlanDto dto, String userId) {
        String slug = (dto.getSlug() != null && !dto.getSlug().isBlank())
                ? slugify(dto.getSlug())
                : slugify(dto.getName());

        if (planRepository.existsBySlug(slug)) {
            slug = slug + "-" + UUID.randomUUID().toString().substring(0, 5);
        }

        List<String> cleanAdvantages = sanitizeAdvantages(dto.getAdvantages());

        MembershipPlan plan = MembershipPlan.builder()
                .name(dto.getName().trim())
                .slug(slug)
                .description(dto.getDescription())
                .price(dto.getPrice())
                .billingPeriod(dto.getBillingPeriod() != null ? dto.getBillingPeriod().trim().toUpperCase() : "MENSAL")
                .advantages(cleanAdvantages)
                .badgeText(dto.getBadgeText())
                .highlighted(Boolean.TRUE.equals(dto.getHighlighted()))
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .whatsappOverride(dto.getWhatsappOverride())
                .active(dto.getActive() == null || dto.getActive())
                .build();

        MembershipPlan saved = planRepository.save(plan);
        log.info("Plano de sócio criado: id={}, nome={}, por usuário={}", saved.getId(), saved.getName(), userId);
        webSocketHandler.broadcastAfterCommit("membership", "CREATED", saved.getId().toString());

        return MembershipMapperHelper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "membership_plans_public", allEntries = true)
    public MembershipPlanDto update(UUID id, UpdateMembershipPlanDto dto, String userId) {
        MembershipPlan plan = planRepository.findActiveById(id)
                .orElseThrow(() -> new BusinessException("Plano de sócio não encontrado para edição."));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            plan.setName(dto.getName().trim());
        }

        if (dto.getSlug() != null && !dto.getSlug().isBlank()) {
            String newSlug = slugify(dto.getSlug());
            if (planRepository.existsBySlugAndIdNot(newSlug, id)) {
                throw new BusinessException("Já existe outro plano cadastrado com este slug: " + newSlug);
            }
            plan.setSlug(newSlug);
        }

        if (dto.getDescription() != null) {
            plan.setDescription(dto.getDescription());
        }

        if (dto.getPrice() != null) {
            plan.setPrice(dto.getPrice());
        }

        if (dto.getBillingPeriod() != null) {
            plan.setBillingPeriod(dto.getBillingPeriod().trim().toUpperCase());
        }

        if (dto.getAdvantages() != null) {
            plan.setAdvantages(sanitizeAdvantages(dto.getAdvantages()));
        }

        if (dto.getBadgeText() != null) {
            plan.setBadgeText(dto.getBadgeText().trim());
        }

        if (dto.getHighlighted() != null) {
            plan.setHighlighted(dto.getHighlighted());
        }

        if (dto.getDisplayOrder() != null) {
            plan.setDisplayOrder(dto.getDisplayOrder());
        }

        if (dto.getWhatsappOverride() != null) {
            plan.setWhatsappOverride(dto.getWhatsappOverride().trim());
        }

        if (dto.getActive() != null) {
            plan.setActive(dto.getActive());
        }

        MembershipPlan saved = planRepository.save(plan);
        log.info("Plano de sócio atualizado: id={}, nome={}, por usuário={}", saved.getId(), saved.getName(), userId);
        webSocketHandler.broadcastAfterCommit("membership", "UPDATED", saved.getId().toString());

        return MembershipMapperHelper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "membership_plans_public", allEntries = true)
    public MembershipPlanDto toggleActive(UUID id, String userId) {
        MembershipPlan plan = planRepository.findActiveById(id)
                .orElseThrow(() -> new BusinessException("Plano de sócio não encontrado."));

        plan.setActive(!plan.isActive());
        MembershipPlan saved = planRepository.save(plan);
        log.info("Status do plano alternado: id={}, ativo={}, por usuário={}", id, saved.isActive(), userId);
        webSocketHandler.broadcastAfterCommit("membership", "UPDATED", id.toString());

        return MembershipMapperHelper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "membership_plans_public", allEntries = true)
    public void delete(UUID id, String userId) {
        MembershipPlan plan = planRepository.findActiveById(id)
                .orElseThrow(() -> new BusinessException("Plano de sócio não encontrado."));

        plan.setDeletedAt(Instant.now());
        plan.setActive(false);
        planRepository.save(plan);

        log.info("Plano de sócio inativado via soft-delete: id={}, por usuário={}", id, userId);
        webSocketHandler.broadcastAfterCommit("membership", "DELETED", id.toString());
    }

    @Transactional
    @CacheEvict(value = "membership_plans_public", allEntries = true)
    public void reorder(List<UUID> planIds, String userId) {
        if (planIds == null || planIds.isEmpty()) return;

        for (int i = 0; i < planIds.size(); i++) {
            UUID planId = planIds.get(i);
            int order = i + 1;
            planRepository.findActiveById(planId).ifPresent(plan -> {
                plan.setDisplayOrder(order);
                planRepository.save(plan);
            });
        }

        log.info("Planos de sócios reordenados por usuário={}", userId);
        webSocketHandler.broadcastAfterCommit("membership", "REORDERED", "all");
    }

    private List<String> sanitizeAdvantages(List<String> raw) {
        if (raw == null) return new ArrayList<>();
        return raw.stream()
                .filter(item -> item != null && !item.trim().isBlank())
                .map(String::trim)
                .toList();
    }

    private String slugify(String input) {
        if (input == null) return "";
        String nowhitespace = WHITESPACE.matcher(input.trim().toLowerCase(Locale.ROOT)).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.replaceAll("-{2,}", "-").replaceAll("^-|-$", "");
    }
}
