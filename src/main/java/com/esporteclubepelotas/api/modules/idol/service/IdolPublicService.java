package com.esporteclubepelotas.api.modules.idol.service;

import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.modules.idol.dto.GetIdolsResponseDto;
import com.esporteclubepelotas.api.modules.idol.dto.IdolDto;
import com.esporteclubepelotas.api.modules.idol.dto.PrevNextIdolResponseDto;
import com.esporteclubepelotas.api.modules.idol.entity.Idol;
import com.esporteclubepelotas.api.modules.idol.entity.IdolCategory;
import com.esporteclubepelotas.api.modules.idol.helper.IdolSpecifications;
import com.esporteclubepelotas.api.modules.idol.repository.IdolRepository;
import com.esporteclubepelotas.api.modules.systemmodule.repository.SystemModuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdolPublicService {

    private final IdolRepository idolRepository;
    private final SystemModuleRepository moduleRepository;

    private boolean isModuleActive() {
        return moduleRepository.findBySlug("idols")
                .map(m -> m.isActive())
                .orElse(true);
    }

    @Transactional(readOnly = true)
    public GetIdolsResponseDto findAll(int page, int limit, String search, IdolCategory category, Boolean isFeatured) {
        boolean active = isModuleActive();
        if (!active) {
            return GetIdolsResponseDto.of(Collections.emptyList(), 0, page, limit, false);
        }

        int pageIndex = Math.max(page - 1, 0);
        int pageSize = limit > 0 ? limit : 12;

        Sort sort = Sort.by(
                Sort.Order.desc("isFeatured"),
                Sort.Order.asc("displayOrder"),
                Sort.Order.desc("startYear"),
                Sort.Order.desc("createdAt")
        );
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);


        Page<Idol> idolsPage = idolRepository.findAll(
                IdolSpecifications.withFilters(search, category, isFeatured, false),
                pageable
        );

        List<IdolDto> dtos = idolsPage.getContent().stream()
                .map(IdolDto::fromEntity)
                .toList();

        return GetIdolsResponseDto.of(dtos, idolsPage.getTotalElements(), page, limit, true);
    }

    @Transactional(readOnly = true)
    public IdolDto findBySlug(String slug) {
        if (!isModuleActive()) {
            throw new ResourceNotFoundException("Módulo de Ídolos", "status", "inativo");
        }

        Idol idol = idolRepository.findBySlugAndIsActiveTrue(slug.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Ídolo", "slug", slug));

        return IdolDto.fromEntity(idol);
    }

    @Transactional(readOnly = true)
    public List<IdolDto> findFeatured(int limit) {
        if (!isModuleActive()) {
            return Collections.emptyList();
        }

        int pageSize = limit > 0 ? limit : 4;
        Pageable pageable = PageRequest.of(0, pageSize);

        return idolRepository.findByIsActiveTrueAndIsFeaturedTrueOrderByDisplayOrderAscCreatedAtDesc(pageable)
                .stream()
                .map(IdolDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrevNextIdolResponseDto getPrevNext(String slug) {
        if (!isModuleActive()) {
            throw new ResourceNotFoundException("Módulo de Ídolos", "status", "inativo");
        }

        Idol current = idolRepository.findBySlugAndIsActiveTrue(slug.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Ídolo", "slug", slug));

        Pageable one = PageRequest.of(0, 1);
        List<Idol> prevList = idolRepository.findPreviousIdol(current.getDisplayOrder(), current.getStartYear(), current.getId(), one);
        List<Idol> nextList = idolRepository.findNextIdol(current.getDisplayOrder(), current.getStartYear(), current.getId(), one);

        IdolDto prev = prevList.isEmpty() ? null : IdolDto.fromEntity(prevList.get(0));
        IdolDto next = nextList.isEmpty() ? null : IdolDto.fromEntity(nextList.get(0));

        return PrevNextIdolResponseDto.builder()
                .previous(prev)
                .next(next)
                .build();
    }
}

