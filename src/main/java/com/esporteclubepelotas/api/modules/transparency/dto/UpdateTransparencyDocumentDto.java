package com.esporteclubepelotas.api.modules.transparency.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransparencyDocumentDto {
    private String title;
    private String description;
    private String category;
    private Integer year;
    private String fileUrl;
    private String fileSize;
    private Boolean published;
}

