package com.esporteclubepelotas.api.modules.symbol.dto;

import com.esporteclubepelotas.api.modules.symbol.entity.SymbolCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubSymbolRequest {

    @NotNull(message = "A categoria do símbolo é obrigatória")
    private SymbolCategory category;

    @NotBlank(message = "O nome do símbolo é obrigatório")
    @Size(max = 150, message = "O nome não pode exceder 150 caracteres")
    private String name;

    @Size(max = 250, message = "O subtítulo não pode exceder 250 caracteres")
    private String subtitle;

    private String description;

    private String imageUrl;

    @Builder.Default
    private List<String> galleryUrls = new ArrayList<>();

    private Integer yearStart;

    private Integer yearEnd;

    @Builder.Default
    private Boolean isOfficial = false;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Integer displayOrder = 0;
}
