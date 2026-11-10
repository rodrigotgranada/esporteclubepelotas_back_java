package com.esporteclubepelotas.api.modules.football.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.modules.football.dto.*;
import com.esporteclubepelotas.api.modules.football.entity.*;
import com.esporteclubepelotas.api.modules.football.helper.FootballMapperHelper;
import com.esporteclubepelotas.api.modules.football.repository.AthleteRepository;
import com.esporteclubepelotas.api.modules.football.repository.SquadMemberRepository;
import com.esporteclubepelotas.api.modules.football.repository.SquadRepository;
import com.esporteclubepelotas.api.modules.match.entity.Championship;
import com.esporteclubepelotas.api.modules.match.repository.ChampionshipRepository;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SquadsAdminService {

    private final SquadRepository squadRepository;
    private final SquadMemberRepository squadMemberRepository;
    private final AthleteRepository athleteRepository;
    private final ChampionshipRepository championshipRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Transactional(readOnly = true)
    public GetSquadsResponseDto findAll(QuerySquadsDto query) {
        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int limit = query.getLimit() != null && query.getLimit() > 0 ? Math.min(50, query.getLimit()) : 20;

        Specification<Squad> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("isActive")));

            if (query.getYear() != null) {
                predicates.add(cb.equal(root.get("year"), query.getYear()));
            }

            if ("PROFESSIONAL".equalsIgnoreCase(query.getScope()) && query.getCategory() == null) {
                predicates.add(root.get("category").in(SquadCategory.PROFESSIONAL, SquadCategory.FEMININE));
            } else if ("YOUTH".equalsIgnoreCase(query.getScope()) && query.getCategory() == null) {
                predicates.add(root.get("category").in(SquadCategory.SUB_20, SquadCategory.SUB_17, SquadCategory.SUB_15));
            }
            if (query.getCategory() != null) {
                predicates.add(cb.equal(root.get("category"), query.getCategory()));
            }

            if (query.getSearch() != null && !query.getSearch().isBlank()) {
                String pattern = "%" + query.getSearch().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(cb.coalesce(root.get("seasonName"), "")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("coachName"), "")), pattern)
                ));
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
                .map(FootballMapperHelper::mapSquad)
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
    public SquadDto findById(UUID id) {
        Squad squad = squadRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Elenco não encontrado."));
        return FootballMapperHelper.mapSquad(squad);
    }

    @Transactional
    public SquadDto create(CreateSquadDto dto, String userId) {
        User creator = (userId != null && !userId.isBlank())
                ? userRepository.findById(userId).orElse(null)
                : null;

        if (Boolean.TRUE.equals(dto.getIsCurrent())) {
            squadRepository.clearAllCurrentSquads(dto.getCategory());
        }

        List<SquadStaffMember> staffMembers = (dto.getStaff() != null ? dto.getStaff() : Collections.<SquadStaffDto>emptyList()).stream()
                .map(s -> SquadStaffMember.builder().role(s.getRole().trim()).name(s.getName().trim()).build())
                .toList();

        Championship championship = resolveChampionship(dto.getChampionshipId(), dto.getSeasonName());
        Athlete coach = resolveCoach(dto.getCoachId(), dto.getCoachName());

        String resolvedSeasonName = championship != null && championship.getName() != null
                ? championship.getName()
                : dto.getSeasonName().trim();
        Integer resolvedYear = championship != null && championship.getSeasonYear() != null
                ? championship.getSeasonYear()
                : dto.getYear();
        String resolvedCoachName = coach != null && coach.getName() != null
                ? coach.getName()
                : (dto.getCoachName() != null ? dto.getCoachName().trim() : null);

        Squad squad = Squad.builder()
                .championship(championship)
                .coach(coach)
                .year(resolvedYear)
                .category(dto.getCategory())
                .seasonName(resolvedSeasonName)
                .coachName(resolvedCoachName)
                .staff(new ArrayList<>(staffMembers))
                .coverUrl(dto.getCoverUrl())
                .isCurrent(Boolean.TRUE.equals(dto.getIsCurrent()))
                .isActive(dto.getIsActive() == null || dto.getIsActive())
                .createdBy(creator)
                .build();

        Squad savedSquad = squadRepository.save(squad);

        if (dto.getMembers() != null && !dto.getMembers().isEmpty()) {
            List<SquadMember> members = buildMemberList(savedSquad, dto.getMembers());
            squadMemberRepository.saveAll(members);
            savedSquad.setMembers(members);
        }

        return FootballMapperHelper.mapSquad(savedSquad);
    }

    @Transactional
    public SquadDto update(UUID id, UpdateSquadDto dto) {
        Squad squad = squadRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Elenco não encontrado para atualização."));

        if (Boolean.TRUE.equals(dto.getIsCurrent())) {
            SquadCategory cat = dto.getCategory() != null ? dto.getCategory() : squad.getCategory();
            squadRepository.clearOtherCurrentSquads(cat, squad.getId());
            squad.setCurrent(true);
        } else if (dto.getIsCurrent() != null) {
            squad.setCurrent(dto.getIsCurrent());
        }

        if (dto.getYear() != null) {
            squad.setYear(dto.getYear());
        }

        if (dto.getCategory() != null) {
            squad.setCategory(dto.getCategory());
        }

        if (dto.getChampionshipId() != null || (dto.getSeasonName() != null && !dto.getSeasonName().isBlank())) {
            Championship championship = resolveChampionship(dto.getChampionshipId(), dto.getSeasonName() != null ? dto.getSeasonName() : squad.getSeasonName());
            if (championship != null) {
                squad.setChampionship(championship);
                squad.setSeasonName(championship.getName());
                if (dto.getYear() == null && championship.getSeasonYear() != null) {
                    squad.setYear(championship.getSeasonYear());
                }
            } else if (dto.getSeasonName() != null && !dto.getSeasonName().isBlank()) {
                squad.setSeasonName(dto.getSeasonName().trim());
            }
        }

        if (dto.getCoachId() != null || dto.getCoachName() != null) {
            Athlete coach = resolveCoach(dto.getCoachId(), dto.getCoachName() != null ? dto.getCoachName() : squad.getCoachName());
            if (coach != null) {
                squad.setCoach(coach);
                squad.setCoachName(coach.getName());
            } else if (dto.getCoachName() != null) {
                squad.setCoachName(dto.getCoachName().trim());
            }
        }

        if (dto.getCoverUrl() != null) {
            squad.setCoverUrl(dto.getCoverUrl());
        }

        if (dto.getIsActive() != null) {
            squad.setActive(dto.getIsActive());
        }

        if (dto.getStaff() != null) {
            List<SquadStaffMember> staffMembers = dto.getStaff().stream()
                    .map(s -> SquadStaffMember.builder().role(s.getRole().trim()).name(s.getName().trim()).build())
                    .toList();
            squad.setStaff(new ArrayList<>(staffMembers));
        }

        if (dto.getMembers() != null) {
            squadMemberRepository.deleteBySquadId(squad.getId());
            List<SquadMember> members = buildMemberList(squad, dto.getMembers());
            squadMemberRepository.saveAll(members);
            squad.setMembers(members);
        }

        Squad saved = squadRepository.save(squad);
        return FootballMapperHelper.mapSquad(saved);
    }

    @Transactional
    public SquadDto setMembers(UUID squadId, SetSquadMembersDto dto) {
        Squad squad = squadRepository.findById(squadId)
                .orElseThrow(() -> new BusinessException("Elenco não encontrado."));

        squadMemberRepository.deleteBySquadId(squad.getId());

        List<SquadMember> members = buildMemberList(squad, dto.getMembers());
        squadMemberRepository.saveAll(members);
        squad.setMembers(members);

        return FootballMapperHelper.mapSquad(squadRepository.save(squad));
    }

    @Transactional
    public SquadDto addOrUpdateMember(UUID squadId, SquadMemberDto memberDto) {
        Squad squad = squadRepository.findById(squadId)
                .orElseThrow(() -> new BusinessException("Elenco não encontrado."));

        UUID athleteUuid = UUID.fromString(memberDto.getAthleteId().trim());
        Athlete athlete = athleteRepository.findById(athleteUuid)
                .orElseThrow(() -> new BusinessException("Atleta não encontrado pelo ID: " + memberDto.getAthleteId()));

        SquadMember member = squadMemberRepository.findBySquadIdAndAthleteId(squad.getId(), athlete.getId())
                .orElseGet(() -> SquadMember.builder().squad(squad).athlete(athlete).build());

        member.setJerseyNumber(memberDto.getJerseyNumber());
        member.setSector(memberDto.getSector());
        member.setRole(memberDto.getRole() != null ? memberDto.getRole() : SquadMemberRole.STARTER);
        member.setDisplayOrder(memberDto.getOrder() != null ? memberDto.getOrder() : 0);

        squadMemberRepository.save(member);

        boolean found = false;
        if (squad.getMembers() == null) {
            squad.setMembers(new ArrayList<>());
        }
        for (SquadMember m : squad.getMembers()) {
            if (m.getAthlete() != null && m.getAthlete().getId().equals(athlete.getId())) {
                m.setJerseyNumber(member.getJerseyNumber());
                m.setSector(member.getSector());
                m.setRole(member.getRole());
                m.setDisplayOrder(member.getDisplayOrder());
                found = true;
                break;
            }
        }
        if (!found) {
            squad.getMembers().add(member);
        }

        return FootballMapperHelper.mapSquad(squad);
    }

    @Transactional
    public SquadDto removeMember(UUID squadId, UUID athleteId) {
        Squad squad = squadRepository.findById(squadId)
                .orElseThrow(() -> new BusinessException("Elenco não encontrado."));

        squadMemberRepository.deleteBySquadIdAndAthleteId(squad.getId(), athleteId);
        if (squad.getMembers() != null) {
            squad.getMembers().removeIf(m -> m.getAthlete() != null && m.getAthlete().getId().equals(athleteId));
        }
        return FootballMapperHelper.mapSquad(squad);
    }

    @Transactional
    public void remove(UUID id) {
        Squad squad = squadRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Elenco não encontrado para exclusão."));
        squad.setActive(false);
        squadRepository.save(squad);
    }

    @Transactional
    public Map<String, String> uploadCover(UUID id, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo enviado.");
        }

        Squad squad = squadRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Elenco não encontrado."));

        String ext = getFileExtension(file.getOriginalFilename());
        String fileName = "cover-" + System.currentTimeMillis() + "." + ext;
        String folder = "squads/" + id;

        String coverUrl = storageService.uploadFile(file, folder, fileName);
        squad.setCoverUrl(coverUrl);
        squadRepository.save(squad);

        return Map.of("coverUrl", coverUrl);
    }

    public Map<String, String> uploadStandaloneCover(MultipartFile file, String folderId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo enviado.");
        }
        String targetFolder = (folderId != null && !folderId.isBlank()) ? folderId.trim() : UUID.randomUUID().toString();
        String ext = getFileExtension(file.getOriginalFilename());
        String fileName = "cover-" + System.currentTimeMillis() + "." + ext;
        String folder = "squads/" + targetFolder;

        String coverUrl = storageService.uploadFile(file, folder, fileName);
        return Map.of("url", coverUrl, "coverUrl", coverUrl);
    }

    private List<SquadMember> buildMemberList(Squad squad, List<SquadMemberDto> memberDtos) {
        List<SquadMember> members = new ArrayList<>();
        if (memberDtos == null) return members;

        for (SquadMemberDto mDto : memberDtos) {
            if (mDto.getAthleteId() == null || mDto.getAthleteId().isBlank()) continue;
            try {
                UUID athleteUuid = UUID.fromString(mDto.getAthleteId().trim());
                athleteRepository.findById(athleteUuid).ifPresent(athlete -> {
                    SquadMember member = SquadMember.builder()
                            .squad(squad)
                            .athlete(athlete)
                            .jerseyNumber(mDto.getJerseyNumber())
                            .sector(mDto.getSector())
                            .role(mDto.getRole() != null ? mDto.getRole() : SquadMemberRole.STARTER)
                            .displayOrder(mDto.getOrder() != null ? mDto.getOrder() : 0)
                            .build();
                    members.add(member);
                });
            } catch (IllegalArgumentException ignored) {}
        }
        return members;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "webp";
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private Championship resolveChampionship(String championshipId, String seasonName) {
        if (championshipId != null && !championshipId.isBlank()) {
            try {
                UUID id = UUID.fromString(championshipId.trim());
                java.util.Optional<Championship> byId = championshipRepository.findActiveById(id);
                if (byId.isPresent()) return byId.get();
            } catch (IllegalArgumentException ignored) {}
        }
        if (seasonName != null && !seasonName.trim().isEmpty()) {
            String clean = seasonName.trim().toLowerCase();
            return championshipRepository.findAllActive().stream()
                    .filter(c -> c.getName().toLowerCase().equals(clean) || clean.contains(c.getName().toLowerCase()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private Athlete resolveCoach(String coachId, String coachName) {
        if (coachId != null && !coachId.isBlank()) {
            try {
                UUID id = UUID.fromString(coachId.trim());
                java.util.Optional<Athlete> byId = athleteRepository.findById(id);
                if (byId.isPresent()) return byId.get();
            } catch (IllegalArgumentException ignored) {}
        }
        if (coachName != null && !coachName.trim().isEmpty()) {
            String clean = coachName.trim().toLowerCase();
            return athleteRepository.findAll().stream()
                    .filter(a -> a.getAthleteType() == AthleteType.STAFF && a.getName().toLowerCase().equals(clean))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
