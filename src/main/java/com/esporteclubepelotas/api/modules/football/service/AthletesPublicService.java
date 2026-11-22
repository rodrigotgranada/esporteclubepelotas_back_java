package com.esporteclubepelotas.api.modules.football.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.modules.football.dto.AthleteDto;
import com.esporteclubepelotas.api.modules.football.dto.AthleteSeasonDto;
import com.esporteclubepelotas.api.modules.football.dto.GetAthletesResponseDto;
import com.esporteclubepelotas.api.modules.football.dto.QueryAthletesDto;
import com.esporteclubepelotas.api.modules.football.entity.Athlete;
import com.esporteclubepelotas.api.modules.football.entity.SquadMember;
import com.esporteclubepelotas.api.modules.football.helper.FootballMapperHelper;
import com.esporteclubepelotas.api.modules.football.repository.AthleteRepository;
import com.esporteclubepelotas.api.modules.football.repository.SquadMemberRepository;
import com.esporteclubepelotas.api.modules.systemmodule.service.SystemModuleService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AthletesPublicService {

    private final AthleteRepository athleteRepository;
    private final SquadMemberRepository squadMemberRepository;
    private final SystemModuleService moduleService;

    private boolean isScopeActive(com.esporteclubepelotas.api.modules.football.entity.AthleteType type, com.esporteclubepelotas.api.modules.football.entity.AthleteGender gender) {
        if (type == com.esporteclubepelotas.api.modules.football.entity.AthleteType.YOUTH) {
            return moduleService.isModuleActive("youth");
        }
        if (gender == com.esporteclubepelotas.api.modules.football.entity.AthleteGender.FEMALE) {
            return moduleService.isModuleActive("female");
        }
        return moduleService.isModuleActive("football");
    }

    @Transactional(readOnly = true)
    public GetAthletesResponseDto findAll(QueryAthletesDto query) {
        boolean active = isScopeActive(query.getAthleteType(), query.getGender());
        if (!active) {
            return GetAthletesResponseDto.builder()
                    .data(Collections.emptyList())
                    .total(0)
                    .page(1)
                    .limit(query.getLimit() != null ? query.getLimit() : 20)
                    .totalPages(0)
                    .isModuleActive(false)
                    .build();
        }

        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int limit = query.getLimit() != null && query.getLimit() > 0 ? Math.min(50, query.getLimit()) : 20;

        Specification<Athlete> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("isActive")));

            if (query.getPosition() != null && !query.getPosition().isBlank()) {
                String pos = query.getPosition().trim().toLowerCase();
                predicates.add(cb.like(cb.lower(root.get("primaryPosition")), "%" + pos + "%"));
            }

            if (query.getSearch() != null && !query.getSearch().isBlank()) {
                String pattern = "%" + query.getSearch().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("nickname")), pattern),
                        cb.like(cb.lower(root.get("primaryPosition")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Athlete> paged = athleteRepository.findAll(
                spec,
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.ASC, "name"))
        );

        List<AthleteDto> dtos = paged.getContent().stream()
                .map(FootballMapperHelper::mapAthlete)
                .toList();

        return GetAthletesResponseDto.builder()
                .data(dtos)
                .total(paged.getTotalElements())
                .page(page)
                .limit(limit)
                .totalPages(paged.getTotalPages() > 0 ? paged.getTotalPages() : 1)
                .isModuleActive(true)
                .build();
    }

    @Transactional(readOnly = true)
    public AthleteDto findBySlug(String slug) {
        if (!moduleService.isModuleActive("football")
                && !moduleService.isModuleActive("youth")
                && !moduleService.isModuleActive("female")) {
            throw new BusinessException("Módulo de Futebol está temporariamente desativado.");
        }

        String clean = slug.trim().toLowerCase();
        Athlete athlete = athleteRepository.findBySlugAndIsActiveTrue(clean)
                .orElseThrow(() -> new BusinessException("Atleta não encontrado no elenco do Pelotas."));

        List<SquadMember> squadMembers = squadMemberRepository.findSeasonsByAthleteId(athlete.getId());

        List<AthleteSeasonDto> seasons = squadMembers.stream().map(m -> AthleteSeasonDto.builder()
                .squadId(m.getSquad().getId().toString())
                .year(m.getSquad().getYear())
                .category(m.getSquad().getCategory())
                .seasonName(m.getSquad().getSeasonName())
                .jerseyNumber(m.getJerseyNumber())
                .sector(m.getSector())
                .role(m.getRole())
                .build()
        ).toList();

        AthleteDto dto = FootballMapperHelper.mapAthlete(athlete);
        dto.setSeasons(seasons);
        return dto;
    }
}
