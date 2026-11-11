package com.esporteclubepelotas.api.modules.transparency.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransparencyDocumentDto {
    private UUID id;
    private String title;
    private String description;
    private String category;
    private Integer year;
    private String fileUrl;
    private String fileSize;
    private boolean published;
    private Instant createdAt;
    private Instant updatedAt;
}

