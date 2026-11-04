package com.esporteclubepelotas.api.modules.user.entity;

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
public class UserPreferences implements Serializable {
    private String shirtSize;
    @Builder.Default
    private boolean receiveNewsletter = true;
    @Builder.Default
    private boolean receiveSms = false;
}

