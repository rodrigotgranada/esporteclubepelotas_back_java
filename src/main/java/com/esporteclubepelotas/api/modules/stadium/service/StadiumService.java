package com.esporteclubepelotas.api.modules.stadium.service;

import com.esporteclubepelotas.api.common.storage.MediaUploadSanitizer;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.audit.annotation.AuditLog;
import com.esporteclubepelotas.api.modules.stadium.dto.*;
import com.esporteclubepelotas.api.modules.stadium.entity.StadiumInfo;
import com.esporteclubepelotas.api.modules.stadium.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StadiumService {

    private final StadiumRepository stadiumRepository;
    private final StorageService storageService;
    private final PortalWebSocketHandler webSocketHandler;

    @Transactional(readOnly = true)
    @Cacheable(value = "stadiumInfo")
    public StadiumResponseDto getStadiumInfo() {
        StadiumInfo stadium = stadiumRepository.findById(1L)
                .orElseGet(this::createDefaultStadium);
        return toResponseDto(stadium);
    }

    @Transactional
    @AuditLog(module = "STADIUM", action = "UPDATE", description = "Atualização dos dados e memorial do Estádio Boca do Lobo")
    @CacheEvict(value = "stadiumInfo", allEntries = true)
    public StadiumResponseDto updateStadiumInfo(UpdateStadiumDto dto) {
        StadiumInfo stadium = stadiumRepository.findById(1L)
                .orElseGet(this::createDefaultStadium);

        stadium.setTitle(dto.getTitle());
        stadium.setSubtitle(dto.getSubtitle());
        stadium.setCoverImageUrl(MediaUploadSanitizer.sanitizeNullableUrl(dto.getCoverImageUrl()));
        stadium.setInaugurationDate(dto.getInaugurationDate());
        stadium.setCapacity(dto.getCapacity());
        stadium.setPitchDimensions(dto.getPitchDimensions());
        stadium.setAddress(dto.getAddress());
        stadium.setMapsDirectUrl(dto.getMapsDirectUrl());
        stadium.setMapsEmbedUrl(dto.getMapsEmbedUrl());
        stadium.setContentHtml(dto.getContentHtml());

        if (dto.getGallery() != null) {
            stadium.setGallery(dto.getGallery());
        }
        if (dto.getFacts() != null) {
            stadium.setFacts(dto.getFacts());
        }
        if (dto.getQuickFacts() != null) {
            stadium.setQuickFacts(dto.getQuickFacts());
        }
        if (dto.getIsActive() != null) {
            stadium.setIsActive(dto.getIsActive());
        }

        StadiumInfo saved = stadiumRepository.save(stadium);
        log.info("Memorial do Estádio Boca do Lobo atualizado com sucesso");

        webSocketHandler.broadcastAfterCommit("stadium", "UPDATED", "1", null);

        return toResponseDto(saved);
    }

    public Map<String, String> uploadCover(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Nenhum arquivo enviado.");
        }
        try {
            String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "cover.jpg";
            String url = storageService.uploadFile(
                    file.getBytes(),
                    "boca-do-lobo-cover-" + System.currentTimeMillis() + "-" + originalFilename,
                    "stadium",
                    file.getContentType()
            );
            return Map.of("url", url);
        } catch (IOException e) {
            log.error("Erro ao fazer upload da capa do estádio: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao processar arquivo da capa", e);
        }
    }

    public Map<String, String> uploadGalleryImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Nenhum arquivo enviado.");
        }
        try {
            String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "gallery.jpg";
            String url = storageService.uploadFile(
                    file.getBytes(),
                    "stadium-gallery-" + System.currentTimeMillis() + "-" + originalFilename,
                    "stadium/gallery",
                    file.getContentType()
            );
            return Map.of("url", url);
        } catch (IOException e) {
            log.error("Erro ao fazer upload da foto de galeria do estádio: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao processar foto da galeria", e);
        }
    }

    private StadiumResponseDto toResponseDto(StadiumInfo entity) {
        return StadiumResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .subtitle(entity.getSubtitle())
                .coverImageUrl(entity.getCoverImageUrl())
                .inaugurationDate(entity.getInaugurationDate())
                .capacity(entity.getCapacity())
                .pitchDimensions(entity.getPitchDimensions())
                .address(entity.getAddress())
                .mapsDirectUrl(entity.getMapsDirectUrl())
                .mapsEmbedUrl(entity.getMapsEmbedUrl())
                .contentHtml(entity.getContentHtml())
                .gallery(entity.getGallery() != null ? entity.getGallery() : new ArrayList<>())
                .facts(entity.getFacts() != null ? entity.getFacts() : new ArrayList<>())
                .quickFacts(entity.getQuickFacts() != null ? entity.getQuickFacts() : new ArrayList<>())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private StadiumInfo createDefaultStadium() {
        List<StadiumQuickFactDto> defaultQuickFacts = List.of(
                new StadiumQuickFactDto("fact-1", "Fundação / Inauguração", "25 de Outubro de 1908", "calendar", 1),
                new StadiumQuickFactDto("fact-2", "Capacidade Oficial", "23.336 espectadores", "users", 2),
                new StadiumQuickFactDto("fact-3", "Dimensões do Gramado", "105m x 68m (Padrão Oficial)", "maximize", 3),
                new StadiumQuickFactDto("fact-4", "Alcunha Mística", "A Boca do Lobo", "flame", 4),
                new StadiumQuickFactDto("fact-5", "Pioneirismo de Refletores", "Desde 1930 (1º do interior do RS)", "zap", 5),
                new StadiumQuickFactDto("fact-6", "Localização Nobre", "Avenida Bento Gonçalves com Gonçalves Chaves", "mapPin", 6)
        );

        StadiumInfo defaultInfo = StadiumInfo.builder()
                .id(1L)
                .title("Estádio Boca do Lobo: A Fortaleza Centenária do Futebol Gaúcho")
                .subtitle("Fundado em 1908, o mais antigo estádio de futebol em atividade do Rio Grande do Sul é o solo sagrado onde a paixão áureo-cerúlea desafia o tempo há mais de 115 anos.")
                .coverImageUrl("https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=1600&q=85")
                .inaugurationDate("25 de Outubro de 1908")
                .capacity("23.336 espectadores")
                .pitchDimensions("105m x 68m (Padrão Oficial)")
                .address("Avenida Bento Gonçalves com Gonçalves Chaves, Centro - Pelotas/RS, CEP 96015-140")
                .mapsDirectUrl("https://www.google.com/maps/search/?api=1&query=Est%C3%A1dio+Boca+do+Lobo+Pelotas")
                .mapsEmbedUrl("https://maps.google.com/maps?q=Est%C3%A1dio+Boca+do+Lobo,+Pelotas+-+RS&t=&z=16&ie=UTF8&iwloc=&output=embed")
                .contentHtml("<p>História do Estádio Boca do Lobo em carregamento...</p>")
                .gallery(new ArrayList<>())
                .facts(new ArrayList<>())
                .quickFacts(defaultQuickFacts)
                .isActive(true)
                .build();
        return stadiumRepository.save(defaultInfo);
    }
}
