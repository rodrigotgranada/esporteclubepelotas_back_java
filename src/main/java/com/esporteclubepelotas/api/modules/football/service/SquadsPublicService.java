package com.esporteclubepelotas.api.modules.football.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.modules.football.dto.GetSquadsResponseDto;
import com.esporteclubepelotas.api.modules.football.dto.QuerySquadsDto;
import com.esporteclubepelotas.api.modules.football.dto.SquadDto;
import com.esporteclubepelotas.api.modules.football.dto.SquadSeasonsResponseDto;
import com.esporteclubepelotas.api.modules.football.entity.Squad;
import com.esporteclubepelotas.api.modules.football.entity.SquadCategory;
import com.esporteclubepelotas.api.modules.football.helper.FootballMapperHelper;
import com.esporteclubepelotas.api.modules.football.repository.SquadRepository;
import com.esporteclubepelotas.api.modules.systemmodule.entity.SystemModule;
import com.esporteclubepelotas.api.modules.systemmodule.repository.SystemModuleRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SquadsPublicService {

    private final SquadRepository squadRepository;
    private final SystemModuleRepository moduleRepository;

    private boolean isModuleActive() {
        return moduleRepository.findBySlug("football")
                .map(SystemModule::isActive)
                .orElse(true);
    }

    @Transactional(readOnly = true)
    public SquadSeasonsResponseDto getSeasons() {
        if (!isModuleActive()) {
            return SquadSeasonsResponseDto.builder()
                    .years(Collections.emptyList())
                    .categories(Collections.emptyList())
                    .categoriesByYear(Collections.emptyMap())
                    .isModuleActive(false)
                    .build();
        }

        List<Object[]> rows = squadRepository.findActiveYearAndCategories();
        Map<Integer, List<String>> categoriesByYear = new LinkedHashMap<>();
        Set<String> allCategories = new LinkedHashSet<>();

        for (Object[] row : rows) {
            Integer year = (Integer) row[0];
            SquadCategory cat = (SquadCategory) row[1];
            String catName = cat.name();

            categoriesByYear.computeIfAbsent(year, k -> new ArrayList<>()).add(catName);
            allCategories.add(catName);
        }

        List<Integer> years = new ArrayList<>(categoriesByYear.keySet());

        return SquadSeasonsResponseDto.builder()
                .years(years)
                .categories(new ArrayList<>(allCategories))
                .categoriesByYear(categoriesByYear)
                .isModuleActive(true)
                .build();
    }

    @Transactional(readOnly = true)
    public GetSquadsResponseDto findAll(QuerySquadsDto query) {
        if (!isModuleActive()) {
            return GetSquadsResponseDto.builder()
                    .data(Collections.emptyList())
                    .total(0)
                    .page(1)
                    .limit(query.getLimit() != null ? query.getLimit() : 20)
                    .totalPages(0)
                    .isModuleActive(false)
                    .build();
        }

        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int limit = query.getLimit() != null && query.getLimit() > 0 ? Math.min(100, query.getLimit()) : 20;

        Specification<Squad> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("isActive")));

            if (query.getYear() != null) {
                predicates.add(cb.equal(root.get("year"), query.getYear()));
            }

            if ("PROFESSIONAL".equalsIgnoreCase(query.getScope()) && query.getCategory() == null) {
                predicates.add(cb.equal(root.get("category"), SquadCategory.PROFESSIONAL));
            } else if (("FEMALE".equalsIgnoreCase(query.getScope()) || "FEMININE".equalsIgnoreCase(query.getScope())) && query.getCategory() == null) {
                predicates.add(cb.equal(root.get("category"), SquadCategory.FEMININE));
            } else if ("YOUTH".equalsIgnoreCase(query.getScope()) && query.getCategory() == null) {
                predicates.add(root.get("category").in(SquadCategory.SUB_20, SquadCategory.SUB_17, SquadCategory.SUB_15));
            }

            if (query.getCategory() != null) {
                predicates.add(cb.equal(root.get("category"), query.getCategory()));
            }

            if (query.getIsCurrent() != null) {
                predicates.add(cb.equal(root.get("isCurrent"), query.getIsCurrent()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(
                Sort.Order.desc("year"),
                Sort.Order.desc("isCurrent"),
                Sort.Order.desc("createdAt")
        );

        Page<Squad> paged = squadRepository.findAll(spec, PageRequest.of(page - 1, limit, sort));

        List<SquadDto> dtos = paged.getContent().stream()
                .map(FootballMapperHelper::organizeSquadSectors)
                .toList();

        return GetSquadsResponseDto.builder()
                .data(dtos)
                .total(paged.getTotalElements())
                .page(page)
                .limit(limit)
                .totalPages(paged.getTotalPages() > 0 ? paged.getTotalPages() : 1)
                .isModuleActive(true)
                .build();
    }

    @Transactional(readOnly = true)
    public SquadDto getCurrent(SquadCategory category) {
        if (!isModuleActive()) {
            return null;
        }

        SquadCategory cat = category != null ? category : SquadCategory.PROFESSIONAL;

        Squad squad = squadRepository.findFirstByCategoryAndIsCurrentTrueAndIsActiveTrue(cat)
                .or(() -> squadRepository.findFirstByCategoryAndIsActiveTrueOrderByYearDescCreatedAtDesc(cat))
                .orElse(null);

        if (squad == null) {
            return null;
        }

        return FootballMapperHelper.organizeSquadSectors(squad);
    }

    @Transactional(readOnly = true)
    public SquadDto getById(UUID id) {
        if (!isModuleActive()) {
            throw new BusinessException("Módulo de Futebol está temporariamente desativado.");
        }

        Squad squad = squadRepository.findById(id)
                .filter(Squad::isActive)
                .orElseThrow(() -> new BusinessException("Elenco não encontrado."));

        return FootballMapperHelper.organizeSquadSectors(squad);
    }

    @Transactional(readOnly = true)
    public SquadDto getByYearAndCategory(int year, SquadCategory category) {
        if (!isModuleActive()) {
            throw new BusinessException("Módulo de Futebol está temporariamente desativado.");
        }

        Squad squad = squadRepository.findByYearAndCategoryAndIsActiveTrue(year, category)
                .orElseThrow(() -> new BusinessException("Elenco de " + year + " (" + category + ") não encontrado."));

        return FootballMapperHelper.organizeSquadSectors(squad);
    }
}
