package com.esporteclubepelotas.api.modules.sponsor.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.sponsor.dto.*;
import com.esporteclubepelotas.api.modules.sponsor.entity.Sponsor;
import com.esporteclubepelotas.api.modules.sponsor.helper.SponsorMapperHelper;
import com.esporteclubepelotas.api.modules.sponsor.repository.SponsorRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SponsorService {

    private final SponsorRepository sponsorRepository;
    private final StorageService storageService;
    private final PortalWebSocketHandler webSocketHandler;

    @Transactional(readOnly = true)
    @Cacheable(value = "sponsors_public", sync = true)
    public List<SponsorDto> findActivePublicSponsors() {
        return sponsorRepository.findActivePublicSponsors(LocalDate.now())
                .stream()
                .map(SponsorMapperHelper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "sponsors_featured", sync = true)
    public List<SponsorDto> findFeaturedPublicSponsors() {
        return sponsorRepository.findFeaturedPublicSponsors(LocalDate.now())
                .stream()
                .map(SponsorMapperHelper::toDto)
                .toList();
    }

    @Transactional
    public void incrementClick(UUID id) {
        sponsorRepository.incrementClicksCount(id);
    }

    @Transactional(readOnly = true)
    public GetSponsorsResponseDto findAll(QuerySponsorDto query) {
        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int limit = query.getLimit() != null && query.getLimit() > 0 ? Math.min(100, query.getLimit()) : 20;

        Specification<Sponsor> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            LocalDate today = LocalDate.now();

            // Filtrar estritamente registros não deletados (Soft Delete)
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (query.getSearch() != null && !query.getSearch().isBlank()) {
                String pattern = "%" + query.getSearch().trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), pattern));
            }

            if (query.getIsFeatured() != null) {
                predicates.add(cb.equal(root.get("featured"), query.getIsFeatured()));
            }

            if (query.getIsActive() != null) {
                predicates.add(cb.equal(root.get("active"), query.getIsActive()));
            }

            if (query.getStatus() != null && !query.getStatus().isBlank()) {
                String st = query.getStatus().trim().toUpperCase();
                switch (st) {
                    case "ACTIVE" -> {
                        predicates.add(cb.isTrue(root.get("active")));
                        predicates.add(cb.or(cb.isNull(root.get("contractStartDate")), cb.lessThanOrEqualTo(root.get("contractStartDate"), today)));
                        predicates.add(cb.or(cb.isNull(root.get("contractEndDate")), cb.greaterThanOrEqualTo(root.get("contractEndDate"), today)));
                    }
                    case "SCHEDULED" -> {
                        predicates.add(cb.isTrue(root.get("active")));
                        predicates.add(cb.greaterThan(root.get("contractStartDate"), today));
                    }
                    case "EXPIRED" -> {
                        predicates.add(cb.isTrue(root.get("active")));
                        predicates.add(cb.lessThan(root.get("contractEndDate"), today));
                    }
                    case "INACTIVE" -> predicates.add(cb.isFalse(root.get("active")));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(
                Sort.Order.desc("featured"),
                Sort.Order.asc("displayOrder"),
                Sort.Order.asc("name")
        );

        Page<Sponsor> paged = sponsorRepository.findAll(spec, PageRequest.of(page - 1, limit, sort));
        List<SponsorDto> dtos = paged.getContent().stream()
                .map(SponsorMapperHelper::toDto)
                .toList();

        return GetSponsorsResponseDto.builder()
                .data(dtos)
                .total(paged.getTotalElements())
                .page(page)
                .limit(limit)
                .totalPages(paged.getTotalPages() > 0 ? paged.getTotalPages() : 1)
                .build();
    }

    @Transactional(readOnly = true)
    public SponsorDto findById(UUID id) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException("Patrocinador não encontrado."));
        return SponsorMapperHelper.toDto(sponsor);
    }

    @Transactional
    @CacheEvict(value = {"sponsors_public", "sponsors_featured"}, allEntries = true)
    public SponsorDto create(CreateSponsorDto dto, String userId) {
        String cleanLogo = dto.getLogoUrl() != null && !dto.getLogoUrl().startsWith("blob:")
                ? dto.getLogoUrl().trim()
                : "";

        String cleanLogoDark = dto.getLogoDarkUrl() != null && !dto.getLogoDarkUrl().startsWith("blob:")
                ? dto.getLogoDarkUrl().trim()
                : null;

        Sponsor sponsor = Sponsor.builder()
                .name(dto.getName().trim())
                .logoUrl(cleanLogo)
                .logoDarkUrl(cleanLogoDark)
                .websiteUrl(dto.getWebsiteUrl() != null ? dto.getWebsiteUrl().trim() : null)
                .featured(Boolean.TRUE.equals(dto.getIsFeatured()))
                .active(!Boolean.FALSE.equals(dto.getIsActive()))
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .contractStartDate(dto.getContractStartDate())
                .contractEndDate(dto.getContractEndDate())
                .clicksCount(0L)
                .build();

        Sponsor saved = sponsorRepository.save(sponsor);
        log.info("Patrocinador criado com sucesso: id={}, nome={}, por usuário={}", saved.getId(), saved.getName(), userId);
        webSocketHandler.broadcastAfterCommit("sponsors", "CREATED", saved.getId().toString());
        return SponsorMapperHelper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = {"sponsors_public", "sponsors_featured"}, allEntries = true)
    public SponsorDto update(UUID id, UpdateSponsorDto dto, String userId) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException("Patrocinador não encontrado."));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            sponsor.setName(dto.getName().trim());
        }
        if (dto.getLogoUrl() != null && !dto.getLogoUrl().startsWith("blob:")) {
            sponsor.setLogoUrl(dto.getLogoUrl().trim());
        }
        if (dto.getLogoDarkUrl() != null && !dto.getLogoDarkUrl().startsWith("blob:")) {
            sponsor.setLogoDarkUrl(dto.getLogoDarkUrl().trim().isEmpty() ? null : dto.getLogoDarkUrl().trim());
        }
        if (dto.getWebsiteUrl() != null) {
            sponsor.setWebsiteUrl(dto.getWebsiteUrl().trim().isEmpty() ? null : dto.getWebsiteUrl().trim());
        }
        if (dto.getIsFeatured() != null) {
            sponsor.setFeatured(dto.getIsFeatured());
        }
        if (dto.getIsActive() != null) {
            sponsor.setActive(dto.getIsActive());
        }
        if (dto.getDisplayOrder() != null) {
            sponsor.setDisplayOrder(dto.getDisplayOrder());
        }
        if (dto.getContractStartDate() != null) {
            sponsor.setContractStartDate(dto.getContractStartDate());
        }
        if (dto.getContractEndDate() != null) {
            sponsor.setContractEndDate(dto.getContractEndDate());
        }

        Sponsor saved = sponsorRepository.save(sponsor);
        log.info("Patrocinador atualizado com sucesso: id={}, nome={}, por usuário={}", saved.getId(), saved.getName(), userId);
        webSocketHandler.broadcastAfterCommit("sponsors", "UPDATED", saved.getId().toString());
        return SponsorMapperHelper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = {"sponsors_public", "sponsors_featured"}, allEntries = true)
    public void delete(UUID id, String userId) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException("Patrocinador não encontrado."));

        // Soft Delete preserva o histórico contratual e a contabilidade de métricas
        sponsor.setDeletedAt(Instant.now());
        sponsor.setActive(false);
        sponsorRepository.save(sponsor);
        log.info("Patrocinador inativado via soft-delete com sucesso: id={}, nome={}, por usuário={}", id, sponsor.getName(), userId);
        webSocketHandler.broadcastAfterCommit("sponsors", "DELETED", id.toString());
    }

    @Transactional
    @CacheEvict(value = {"sponsors_public", "sponsors_featured"}, allEntries = true)
    public void reorder(List<SponsorReorderItemDto> items, String userId) {
        if (items == null || items.isEmpty()) return;

        for (SponsorReorderItemDto item : items) {
            if (item.getId() != null && item.getDisplayOrder() != null) {
                sponsorRepository.findById(item.getId()).ifPresent(sponsor -> {
                    if (sponsor.getDeletedAt() == null) {
                        sponsor.setDisplayOrder(item.getDisplayOrder());
                        sponsorRepository.save(sponsor);
                    }
                });
            }
        }
        log.info("Patrocinadores reordenados com sucesso por usuário={}", userId);
        webSocketHandler.broadcastAfterCommit("sponsors", "REORDERED", "all");
    }

    public Map<String, String> uploadLogo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de logotipo não informado.");
        }
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "logo.png";
        String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : ".png";
        String fileName = "sponsor-logo-" + UUID.randomUUID() + ext;
        String url = storageService.uploadFile(file, "sponsors", fileName);
        return Map.of("url", url);
    }
}
