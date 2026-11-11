package com.esporteclubepelotas.api.modules.transparency.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransparencyBoardMemberDto {
    private String name;
    private String roleTitle;
    private String boardType;
    private String termPeriod;
    private String photoUrl;
    private Integer displayOrder;
    private Boolean active;
}

