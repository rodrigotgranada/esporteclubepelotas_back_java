package com.esporteclubepelotas.api.modules.football.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SquadStaffMember implements Serializable {

    private String role;
    private String name;
}

