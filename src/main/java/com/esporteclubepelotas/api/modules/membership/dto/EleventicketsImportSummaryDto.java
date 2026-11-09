package com.esporteclubepelotas.api.modules.membership.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EleventicketsImportSummaryDto {

    private int totalRows;
    private int usersUpdated;
    private int usersActivated;
    private int usersDeactivated;
    private int cacheInserted;
    private int manualOverridesPreserved;
    private int errorsCount;

    @Builder.Default
    private List<String> errorMessages = new ArrayList<>();
}

