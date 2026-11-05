package com.esporteclubepelotas.api.modules.idol.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdolStaffStats implements Serializable {
    private String roleName;
    private String yearsOfDedication;
    private String backstageStory;
}

