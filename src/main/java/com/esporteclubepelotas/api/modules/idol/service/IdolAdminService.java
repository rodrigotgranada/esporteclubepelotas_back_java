package com.esporteclubepelotas.api.modules.idol.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.modules.idol.dto.*;
import com.esporteclubepelotas.api.modules.idol.entity.Idol;
import com.esporteclubepelotas.api.modules.idol.entity.IdolCategory;
import com.esporteclubepelotas.api.modules.idol.entity.IdolGalleryItem;
import com.esporteclubepelotas.api.modules.idol.helper.IdolSlugHelper;
import com.esporteclubepelotas.api.modules.idol.repository.IdolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdolAdminService {

    private final IdolRepository idolRepository;
    private final StorageService storageService;

    @Transactional(readOnly = true)
    public GetIdolsResponseDto findAll(int page, int limit, String search, IdolCategory category, Boolean isFeatured) {
        int pageIndex = Math.max(page - 1, 0);
        int pageSize = limit > 0 ? limit : 20;

        Sort sort = Sort.by(
                Sort.Order.asc("displayOrder"),
                Sort.Order.desc("isFeatured"),
                Sort.Order.desc("startYear"),
                Sort.Order.desc("createdAt")
        );
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

        String searchClean = (search != null && !search.isBlank()) ? search.trim() : null;
        Page<Idol> idolsPage = idolRepository.findWithFilters(searchClean, category, isFeatured, true, pageable);

        List<IdolDto> dtos = idolsPage.getContent().stream()
                .map(IdolDto::fromEntity)
                .toList();

        return GetIdolsResponseDto.of(dtos, idolsPage.getTotalElements(), page, limit, null);
    }

    @Transactional(readOnly = true)
    public IdolDto findById(String id) {
        Idol idol = idolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ídolo", "id", id));
        return IdolDto.fromEntity(idol);
    }

    @Transactional
    public IdolDto create(CreateAdminIdolDto dto, String userId) {
        String baseForSlug = (dto.getSlug() != null && !dto.getSlug().isBlank()) ? dto.getSlug()
                : (dto.getNickname() != null && !dto.getNickname().isBlank()) ? dto.getNickname()
                : dto.getName();

        String uniqueSlug = IdolSlugHelper.generateUniqueSlug(baseForSlug, idolRepository, null);

        List<String> galleryUrls = new ArrayList<>();
        List<IdolGalleryItem> galleryItems = new ArrayList<>();

        if (dto.getGalleryItems() != null && !dto.getGalleryItems().isEmpty()) {
            galleryItems = new ArrayList<>(dto.getGalleryItems());
            galleryUrls = galleryItems.stream().map(IdolGalleryItem::getUrl).toList();
        } else if (dto.getGallery() != null && !dto.getGallery().isEmpty()) {
            galleryUrls = new ArrayList<>(dto.getGallery());
            galleryItems = galleryUrls.stream().map(url -> new IdolGalleryItem(url, "")).toList();
        }

        Integer startYear = dto.getStartYear();
        Integer endYear = dto.getEndYear();

        if (dto.getPeriods() != null && !dto.getPeriods().isEmpty()) {
            if (startYear == null) {
                startYear = dto.getPeriods().get(0).getStartYear();
            }
            if (endYear == null) {
                endYear = dto.getPeriods().get(dto.getPeriods().size() - 1).getEndYear();
            }
        }

        Idol idol = Idol.builder()
                .name(dto.getName().trim())
                .nickname(dto.getNickname() != null ? dto.getNickname().trim() : null)
                .slug(uniqueSlug)
                .category(dto.getCategory())
                .photoUrl(dto.getPhotoUrl())
                .gallery(galleryUrls)
                .galleryItems(galleryItems)
                .startYear(startYear != null ? startYear : 1908)
                .endYear(endYear)
                .periods(dto.getPeriods() != null ? dto.getPeriods() : new ArrayList<>())
                .isActiveInClub(Boolean.TRUE.equals(dto.getIsActiveInClub()))
                .birthDate(dto.getBirthDate())
                .deathDate(dto.getDeathDate())
                .birthPlace(dto.getBirthPlace())
                .quote(dto.getQuote())
                .biography(dto.getBiography())
                .playerStats(dto.getPlayerStats())
                .coachStats(dto.getCoachStats())
                .staffStats(dto.getStaffStats())
                .fanStats(dto.getFanStats())
                .isFeatured(Boolean.TRUE.equals(dto.getIsFeatured()))
                .displayOrder(dto.getOrder() != null ? dto.getOrder() : 0)
                .isActive(true)
                .createdBy(userId)
                .lastEditedBy(userId)
                .build();

        return IdolDto.fromEntity(idolRepository.save(idol));
    }

    @Transactional
    public IdolDto update(String id, UpdateAdminIdolDto dto, String userId) {
        Idol idol = idolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ídolo", "id", id));

        if (dto.getSlug() != null && !dto.getSlug().isBlank() && !dto.getSlug().equalsIgnoreCase(idol.getSlug())) {
            idol.setSlug(IdolSlugHelper.generateUniqueSlug(dto.getSlug(), idolRepository, id));
        }

        if (dto.getName() != null) idol.setName(dto.getName().trim());
        if (dto.getNickname() != null) idol.setNickname(dto.getNickname().trim());
        if (dto.getCategory() != null) idol.setCategory(dto.getCategory());
        if (dto.getPhotoUrl() != null) idol.setPhotoUrl(dto.getPhotoUrl());

        if (dto.getGalleryItems() != null) {
            idol.setGalleryItems(dto.getGalleryItems());
            idol.setGallery(dto.getGalleryItems().stream().map(IdolGalleryItem::getUrl).toList());
        } else if (dto.getGallery() != null) {
            idol.setGallery(dto.getGallery());
            idol.setGalleryItems(dto.getGallery().stream().map(url -> new IdolGalleryItem(url, "")).toList());
        }

        if (dto.getPeriods() != null) {
            idol.setPeriods(dto.getPeriods());
            if (!dto.getPeriods().isEmpty() && dto.getStartYear() == null) {
                idol.setStartYear(dto.getPeriods().get(0).getStartYear());
                if (dto.getPeriods().get(dto.getPeriods().size() - 1).getEndYear() != null) {
                    idol.setEndYear(dto.getPeriods().get(dto.getPeriods().size() - 1).getEndYear());
                }
            }
        }

        if (dto.getStartYear() != null) idol.setStartYear(dto.getStartYear());
        if (dto.getEndYear() != null) idol.setEndYear(dto.getEndYear());
        if (dto.getIsActiveInClub() != null) idol.setActiveInClub(dto.getIsActiveInClub());
        if (dto.getBirthDate() != null) idol.setBirthDate(dto.getBirthDate());
        if (dto.getDeathDate() != null) idol.setDeathDate(dto.getDeathDate());
        if (dto.getBirthPlace() != null) idol.setBirthPlace(dto.getBirthPlace());
        if (dto.getQuote() != null) idol.setQuote(dto.getQuote());
        if (dto.getBiography() != null) idol.setBiography(dto.getBiography());
        if (dto.getPlayerStats() != null) idol.setPlayerStats(dto.getPlayerStats());
        if (dto.getCoachStats() != null) idol.setCoachStats(dto.getCoachStats());
        if (dto.getStaffStats() != null) idol.setStaffStats(dto.getStaffStats());
        if (dto.getFanStats() != null) idol.setFanStats(dto.getFanStats());
        if (dto.getIsFeatured() != null) idol.setFeatured(dto.getIsFeatured());
        if (dto.getOrder() != null) idol.setDisplayOrder(dto.getOrder());
        if (dto.getIsActive() != null) idol.setActive(dto.getIsActive());

        idol.setLastEditedBy(userId);

        return IdolDto.fromEntity(idolRepository.save(idol));
    }

    @Transactional
    public Map<String, Object> softDelete(String id) {
        Idol idol = idolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ídolo", "id", id));

        idol.setActive(false);
        idolRepository.save(idol);

        return Map.of("success", true, "message", "Ídolo removido do Memorial com sucesso.");
    }

    @Transactional
    public Map<String, String> uploadPhoto(String id, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Nenhum arquivo enviado.");
        }

        Idol idol = idolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ídolo", "id", id));

        String fileName = "photo-" + System.currentTimeMillis() + ".jpg";
        String folder = "idols/" + id;
        String url = storageService.uploadFile(file, folder, fileName);

        idol.setPhotoUrl(url);
        idolRepository.save(idol);

        return Map.of("photoUrl", url, "message", "Foto do ídolo atualizada com sucesso.");
    }

    @Transactional
    public Map<String, Object> uploadGallery(String id, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException("Nenhum arquivo enviado.");
        }

        Idol idol = idolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ídolo", "id", id));

        List<String> newUrls = new ArrayList<>();
        List<IdolGalleryItem> currentItems = idol.getGalleryItems() != null ? new ArrayList<>(idol.getGalleryItems()) : new ArrayList<>();
        List<String> currentUrls = idol.getGallery() != null ? new ArrayList<>(idol.getGallery()) : new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = "gallery-" + UUID.randomUUID().toString() + ".jpg";
            String url = storageService.uploadFile(file, "idols/" + id + "/gallery", fileName);
            newUrls.add(url);
            currentUrls.add(url);
            currentItems.add(new IdolGalleryItem(url, ""));
        }

        idol.setGallery(currentUrls);
        idol.setGalleryItems(currentItems);
        idolRepository.save(idol);

        return Map.of(
                "gallery", currentUrls,
                "galleryItems", currentItems,
                "message", "Fotos adicionadas à galeria histórica com sucesso."
        );
    }
}

