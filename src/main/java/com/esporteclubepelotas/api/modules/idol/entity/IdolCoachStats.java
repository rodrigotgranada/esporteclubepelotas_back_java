package com.esporteclubepelotas.api.modules.idol.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdolCoachStats implements Serializable {
    @Builder.Default
    private Integer matchesCount = 0;
    @Builder.Default
    private Integer winsCount = 0;
    @Builder.Default
    private Integer drawsCount = 0;
    @Builder.Default
    private Integer lossesCount = 0;
    @Builder.Default
    private List<String> titles = new ArrayList<>();
    private String tacticalStyle;
}

