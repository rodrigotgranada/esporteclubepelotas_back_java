package com.esporteclubepelotas.api.modules.football.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.modules.football.dto.AthleteDto;
import com.esporteclubepelotas.api.modules.football.dto.CreateAthleteDto;
import com.esporteclubepelotas.api.modules.football.dto.GetAthletesResponseDto;
import com.esporteclubepelotas.api.modules.football.dto.QueryAthletesDto;
import com.esporteclubepelotas.api.modules.football.dto.UpdateAthleteDto;
import com.esporteclubepelotas.api.modules.football.entity.Athlete;
import com.esporteclubepelotas.api.modules.football.entity.AthleteGender;
import com.esporteclubepelotas.api.modules.football.entity.AthleteType;
import com.esporteclubepelotas.api.modules.football.entity.PreferredFoot;
import com.esporteclubepelotas.api.modules.football.helper.AthletesSlugHelper;
import com.esporteclubepelotas.api.modules.football.helper.FootballMapperHelper;
import com.esporteclubepelotas.api.modules.football.repository.AthleteRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AthletesAdminService {

    private final AthleteRepository athleteRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Transactional(readOnly = true)
    public GetAthletesResponseDto findAll(QueryAthletesDto query) {
        int page = query.getPage() != null && query.getPage() > 0 ? query.getPage() : 1;
        int limit = query.getLimit() != null && query.getLimit() > 0 ? Math.min(50, query.getLimit()) : 20;

        Specification<Athlete> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), query.getIsActive()));
            }
            if (query.getGender() != null) {
                predicates.add(cb.equal(root.get("gender"), query.getGender()));
            }
            if (query.getAthleteType() != null) {
                predicates.add(cb.equal(root.get("athleteType"), query.getAthleteType()));
            }

            if (query.getGender() != null) {
                predicates.add(cb.equal(root.get("gender"), query.getGender()));
            }

            if (query.getAthleteType() != null) {
                predicates.add(cb.equal(root.get("athleteType"), query.getAthleteType()));
            }

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
    public AthleteDto findById(UUID id) {
        Athlete athlete = athleteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Atleta não encontrado."));
        return FootballMapperHelper.mapAthlete(athlete);
    }

    @Transactional
    public AthleteDto create(CreateAthleteDto dto, String userId) {
        User creator = (userId != null && !userId.isBlank())
                ? userRepository.findById(userId).orElse(null)
                : null;

        String slug = (dto.getSlug() != null && !dto.getSlug().isBlank())
                ? AthletesSlugHelper.slugify(dto.getSlug())
                : AthletesSlugHelper.generateUniqueAthleteSlug(athleteRepository, dto.getName(), dto.getNickname(), null);

        LocalDate birth = null;
        if (dto.getBirthDate() != null && !dto.getBirthDate().isBlank()) {
            birth = LocalDate.parse(dto.getBirthDate().substring(0, 10));
        }

        Athlete athlete = Athlete.builder()
                .name(dto.getName().trim())
                .nickname(dto.getNickname() != null ? dto.getNickname().trim() : null)
                .slug(slug)
                .photoUrl(dto.getPhotoUrl())
                .gender(dto.getGender() != null ? dto.getGender() : AthleteGender.MALE)
                .athleteType(dto.getAthleteType() != null ? dto.getAthleteType() : AthleteType.PROFESSIONAL)
                .youthCategory(dto.getYouthCategory())
                .entryYear(dto.getEntryYear())
                .staffRole(dto.getStaffRole() != null ? dto.getStaffRole().trim() : null)
                .birthDate(birth)
                .birthPlace(dto.getBirthPlace() != null ? dto.getBirthPlace().trim() : null)
                .height(dto.getHeight())
                .weight(dto.getWeight())
                .preferredFoot(dto.getPreferredFoot() != null ? dto.getPreferredFoot() : PreferredFoot.RIGHT)
                .primaryPosition(dto.getPrimaryPosition() != null && !dto.getPrimaryPosition().isBlank() ? dto.getPrimaryPosition().trim() : (dto.getStaffRole() != null ? dto.getStaffRole().trim() : "Comissão Técnica"))
                .secondaryPositions(dto.getSecondaryPositions() != null ? dto.getSecondaryPositions() : Collections.emptyList())
                .pastClubs(dto.getPastClubs() != null ? dto.getPastClubs() : Collections.emptyList())
                .biography(dto.getBiography())
                .instagram(dto.getInstagram() != null ? dto.getInstagram().trim() : null)
                .isActive(dto.getIsActive() == null || dto.getIsActive())
                .createdBy(creator)
                .build();

        Athlete saved = athleteRepository.save(athlete);
        return FootballMapperHelper.mapAthlete(saved);
    }

    @Transactional
    public AthleteDto update(UUID id, UpdateAthleteDto dto) {
        Athlete athlete = athleteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Atleta não encontrado para atualização."));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            athlete.setName(dto.getName().trim());
        }

        if (dto.getNickname() != null) {
            athlete.setNickname(dto.getNickname().trim());
        }

        if (dto.getSlug() != null && !dto.getSlug().isBlank() && !dto.getSlug().trim().equalsIgnoreCase(athlete.getSlug())) {
            String newSlug = AthletesSlugHelper.generateUniqueAthleteSlug(athleteRepository, dto.getSlug(), null, athlete.getId());
            athlete.setSlug(newSlug);
        } else if (dto.getName() != null || dto.getNickname() != null) {
            String candidateName = dto.getName() != null ? dto.getName() : athlete.getName();
            String candidateNick = dto.getNickname() != null ? dto.getNickname() : athlete.getNickname();
            String updatedSlug = AthletesSlugHelper.generateUniqueAthleteSlug(athleteRepository, candidateName, candidateNick, athlete.getId());
            athlete.setSlug(updatedSlug);
        }

        if (dto.getPhotoUrl() != null) {
            athlete.setPhotoUrl(dto.getPhotoUrl());
        }

        if (dto.getGender() != null) {
            athlete.setGender(dto.getGender());
        }

        if (dto.getAthleteType() != null) {
            athlete.setAthleteType(dto.getAthleteType());
        }

        if (dto.getYouthCategory() != null) {
            athlete.setYouthCategory(dto.getYouthCategory());
        }

        if (dto.getEntryYear() != null) {
            athlete.setEntryYear(dto.getEntryYear());
        }

        if (dto.getStaffRole() != null) {
            athlete.setStaffRole(dto.getStaffRole().trim());
        }

        if (dto.getBirthDate() != null) {
            if (dto.getBirthDate().isBlank()) {
                athlete.setBirthDate(null);
            } else {
                athlete.setBirthDate(LocalDate.parse(dto.getBirthDate().substring(0, 10)));
            }
        }

        if (dto.getBirthPlace() != null) {
            athlete.setBirthPlace(dto.getBirthPlace().trim());
        }

        if (dto.getHeight() != null) {
            athlete.setHeight(dto.getHeight());
        }

        if (dto.getWeight() != null) {
            athlete.setWeight(dto.getWeight());
        }

        if (dto.getPreferredFoot() != null) {
            athlete.setPreferredFoot(dto.getPreferredFoot());
        }

        if (dto.getPrimaryPosition() != null && !dto.getPrimaryPosition().isBlank()) {
            athlete.setPrimaryPosition(dto.getPrimaryPosition().trim());
        }

        if (dto.getSecondaryPositions() != null) {
            athlete.setSecondaryPositions(dto.getSecondaryPositions());
        }

        if (dto.getPastClubs() != null) {
            athlete.setPastClubs(dto.getPastClubs());
        }

        if (dto.getBiography() != null) {
            athlete.setBiography(dto.getBiography());
        }

        if (dto.getInstagram() != null) {
            athlete.setInstagram(dto.getInstagram().trim());
        }

        if (dto.getIsActive() != null) {
            athlete.setActive(dto.getIsActive());
        }

        Athlete saved = athleteRepository.save(athlete);
        return FootballMapperHelper.mapAthlete(saved);
    }

    @Transactional
    public void delete(UUID id) {
        Athlete athlete = athleteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Atleta não encontrado para exclusão."));
        athlete.setActive(false);
        athleteRepository.save(athlete);
    }

    @Transactional
    public void remove(UUID id) {
        delete(id);
    }

    @Transactional
    public Map<String, String> uploadPhoto(UUID id, MultipartFile file) {
        Athlete athlete = athleteRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Atleta não encontrado para upload de foto."));

        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo enviado.");
        }

        String path = "athletes/" + id + "/" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
        String photoUrl = storageService.uploadFile(file, path);

        athlete.setPhotoUrl(photoUrl);
        athleteRepository.save(athlete);

        return Map.of("photoUrl", photoUrl);
    }
}
