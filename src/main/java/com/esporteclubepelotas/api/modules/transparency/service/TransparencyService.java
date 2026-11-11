package com.esporteclubepelotas.api.modules.transparency.service;

import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.storage.MediaUploadSanitizer;
import com.esporteclubepelotas.api.common.storage.StorageService;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.transparency.dto.*;
import com.esporteclubepelotas.api.modules.transparency.entity.TransparencyBoardMember;
import com.esporteclubepelotas.api.modules.transparency.entity.TransparencyDocument;
import com.esporteclubepelotas.api.modules.transparency.repository.TransparencyBoardMemberRepository;
import com.esporteclubepelotas.api.modules.transparency.repository.TransparencyDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransparencyService {

    private final TransparencyDocumentRepository documentRepository;
    private final TransparencyBoardMemberRepository boardMemberRepository;
    private final StorageService storageService;
    private final PortalWebSocketHandler webSocketHandler;

    // --- MÉTODOS PÚBLICOS ---

    @Transactional(readOnly = true)
    public List<TransparencyDocumentDto> listPublicDocuments(String category, Integer year) {
        return documentRepository.findPublishedWithFilters(
                category != null && !category.isBlank() ? category.trim().toUpperCase() : null,
                year
        ).stream().map(this::toDocumentDto).toList();
    }

    @Transactional(readOnly = true)
    public List<TransparencyBoardMemberDto> listPublicBoardMembers(String boardType) {
        if (boardType != null && !boardType.isBlank()) {
            return boardMemberRepository.findByActiveTrueAndBoardTypeOrderByDisplayOrderAscNameAsc(boardType.trim().toUpperCase())
                    .stream().map(this::toBoardMemberDto).toList();
        }
        return boardMemberRepository.findByActiveTrueOrderByDisplayOrderAscNameAsc()
                .stream().map(this::toBoardMemberDto).toList();
    }

    // --- MÉTODOS ADMINISTRATIVOS: DOCUMENTOS ---

    @Transactional(readOnly = true)
    public List<TransparencyDocumentDto> listAllAdminDocuments() {
        return documentRepository.findAllByOrderByYearDescCreatedAtDesc()
                .stream().map(this::toDocumentDto).toList();
    }

    @Transactional
    public TransparencyDocumentDto createDocument(CreateTransparencyDocumentDto dto) {
        if (MediaUploadSanitizer.isBlobUrl(dto.getFileUrl())) {
            throw new com.esporteclubepelotas.api.common.exception.BusinessException("URLs temporárias blob: não são permitidas.");
        }

        TransparencyDocument doc = TransparencyDocument.builder()
                .title(dto.getTitle().trim())
                .description(dto.getDescription())
                .category(dto.getCategory().trim().toUpperCase())
                .year(dto.getYear())
                .fileUrl(dto.getFileUrl().trim())
                .fileSize(dto.getFileSize())
                .published(dto.isPublished())
                .build();

        doc = documentRepository.save(doc);
        webSocketHandler.broadcastAfterCommit("transparency", "DOCUMENT_CREATED", doc.getId().toString());
        return toDocumentDto(doc);
    }

    @Transactional
    public TransparencyDocumentDto updateDocument(UUID id, UpdateTransparencyDocumentDto dto) {
        TransparencyDocument doc = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento de transparência não encontrado: " + id));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            doc.setTitle(dto.getTitle().trim());
        }
        if (dto.getDescription() != null) {
            doc.setDescription(dto.getDescription());
        }
        if (dto.getCategory() != null && !dto.getCategory().isBlank()) {
            doc.setCategory(dto.getCategory().trim().toUpperCase());
        }
        if (dto.getYear() != null) {
            doc.setYear(dto.getYear());
        }
        if (dto.getFileUrl() != null && !dto.getFileUrl().isBlank()) {
            if (MediaUploadSanitizer.isBlobUrl(dto.getFileUrl())) {
                throw new com.esporteclubepelotas.api.common.exception.BusinessException("URLs temporárias blob: não são permitidas.");
            }
            doc.setFileUrl(dto.getFileUrl().trim());
        }
        if (dto.getFileSize() != null) {
            doc.setFileSize(dto.getFileSize());
        }
        if (dto.getPublished() != null) {
            doc.setPublished(dto.getPublished());
        }

        doc = documentRepository.save(doc);
        webSocketHandler.broadcastAfterCommit("transparency", "DOCUMENT_UPDATED", doc.getId().toString());
        return toDocumentDto(doc);
    }

    @Transactional
    public void deleteDocument(UUID id) {
        TransparencyDocument doc = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado: " + id));
        documentRepository.delete(doc);
        webSocketHandler.broadcastAfterCommit("transparency", "DOCUMENT_DELETED", id.toString());
    }

    public Map<String, String> uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo é obrigatório para upload.");
        }
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "documento.pdf";
        String extension = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".pdf";
        String uniqueName = "transparency/" + UUID.randomUUID() + extension;

        String uploadedUrl = storageService.uploadFile(file, uniqueName);
        long sizeInBytes = file.getSize();
        String formattedSize = formatFileSize(sizeInBytes);

        return Map.of(
                "url", uploadedUrl,
                "fileSize", formattedSize,
                "originalName", originalFilename
        );
    }

    // --- MÉTODOS ADMINISTRATIVOS: CONSELHEIROS / DIRETORIA ---

    @Transactional(readOnly = true)
    public List<TransparencyBoardMemberDto> listAllAdminBoardMembers() {
        return boardMemberRepository.findAllByOrderByDisplayOrderAscNameAsc()
                .stream().map(this::toBoardMemberDto).toList();
    }

    @Transactional
    public TransparencyBoardMemberDto createBoardMember(CreateTransparencyBoardMemberDto dto) {
        String cleanPhoto = MediaUploadSanitizer.sanitizeNullableUrl(dto.getPhotoUrl());

        TransparencyBoardMember member = TransparencyBoardMember.builder()
                .name(dto.getName().trim())
                .roleTitle(dto.getRoleTitle().trim())
                .boardType(dto.getBoardType().trim().toUpperCase())
                .termPeriod(dto.getTermPeriod())
                .photoUrl(cleanPhoto)
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .active(dto.isActive())
                .build();

        member = boardMemberRepository.save(member);
        webSocketHandler.broadcastAfterCommit("transparency", "MEMBER_CREATED", member.getId().toString());
        return toBoardMemberDto(member);
    }

    @Transactional
    public TransparencyBoardMemberDto updateBoardMember(UUID id, UpdateTransparencyBoardMemberDto dto) {
        TransparencyBoardMember member = boardMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro de governança não encontrado: " + id));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            member.setName(dto.getName().trim());
        }
        if (dto.getRoleTitle() != null && !dto.getRoleTitle().isBlank()) {
            member.setRoleTitle(dto.getRoleTitle().trim());
        }
        if (dto.getBoardType() != null && !dto.getBoardType().isBlank()) {
            member.setBoardType(dto.getBoardType().trim().toUpperCase());
        }
        if (dto.getTermPeriod() != null) {
            member.setTermPeriod(dto.getTermPeriod());
        }
        if (dto.getPhotoUrl() != null) {
            member.setPhotoUrl(MediaUploadSanitizer.sanitizeNullableUrl(dto.getPhotoUrl()));
        }
        if (dto.getDisplayOrder() != null) {
            member.setDisplayOrder(dto.getDisplayOrder());
        }
        if (dto.getActive() != null) {
            member.setActive(dto.getActive());
        }

        member = boardMemberRepository.save(member);
        webSocketHandler.broadcastAfterCommit("transparency", "MEMBER_UPDATED", member.getId().toString());
        return toBoardMemberDto(member);
    }

    @Transactional
    public void deleteBoardMember(UUID id) {
        TransparencyBoardMember member = boardMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado: " + id));
        boardMemberRepository.delete(member);
        webSocketHandler.broadcastAfterCommit("transparency", "MEMBER_DELETED", id.toString());
    }

    // --- CONVERSORES AUXILIARES ---

    private TransparencyDocumentDto toDocumentDto(TransparencyDocument doc) {
        return TransparencyDocumentDto.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .description(doc.getDescription())
                .category(doc.getCategory())
                .year(doc.getYear())
                .fileUrl(doc.getFileUrl())
                .fileSize(doc.getFileSize())
                .published(doc.isPublished())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    private TransparencyBoardMemberDto toBoardMemberDto(TransparencyBoardMember member) {
        return TransparencyBoardMemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .roleTitle(member.getRoleTitle())
                .boardType(member.getBoardType())
                .termPeriod(member.getTermPeriod())
                .photoUrl(member.getPhotoUrl())
                .displayOrder(member.getDisplayOrder())
                .active(member.isActive())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
