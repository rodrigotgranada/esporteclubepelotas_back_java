package com.esporteclubepelotas.api.modules.football.helper;

import com.esporteclubepelotas.api.modules.football.dto.*;
import com.esporteclubepelotas.api.modules.football.entity.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FootballMapperHelper {

    private FootballMapperHelper() {}

    public static Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) return null;
        LocalDate today = LocalDate.now();
        if (birthDate.isAfter(today)) return null;
        return Period.between(birthDate, today).getYears();
    }

    public static AthleteDto mapAthlete(Athlete athlete) {
        if (athlete == null) return null;
        String idStr = athlete.getId() != null ? athlete.getId().toString() : null;

        return AthleteDto.builder()
                .id(idStr)
                .mongoId(idStr)
                .name(athlete.getName())
                .nickname(athlete.getNickname())
                .slug(athlete.getSlug())
                .photoUrl(athlete.getPhotoUrl())
                .gender(athlete.getGender())
                .athleteType(athlete.getAthleteType())
                .youthCategory(athlete.getYouthCategory())
                .entryYear(athlete.getEntryYear())
                .birthDate(athlete.getBirthDate() != null ? athlete.getBirthDate().toString() : null)
                .age(calculateAge(athlete.getBirthDate()))
                .birthPlace(athlete.getBirthPlace())
                .height(athlete.getHeight())
                .weight(athlete.getWeight())
                .preferredFoot(athlete.getPreferredFoot())
                .primaryPosition(athlete.getPrimaryPosition())
                .secondaryPositions(athlete.getSecondaryPositions() != null ? athlete.getSecondaryPositions() : Collections.emptyList())
                .pastClubs(athlete.getPastClubs() != null ? athlete.getPastClubs() : Collections.emptyList())
                .biography(athlete.getBiography())
                .instagram(athlete.getInstagram())
                .isActive(athlete.isActive())
                .createdAt(athlete.getCreatedAt())
                .updatedAt(athlete.getUpdatedAt())
                .build();
    }

    public static SquadMemberDto mapSquadMember(SquadMember member) {
        if (member == null) return null;

        AthleteDto athleteDto = member.getAthlete() != null ? mapAthlete(member.getAthlete()) : null;
        String athleteId = member.getAthlete() != null && member.getAthlete().getId() != null
                ? member.getAthlete().getId().toString()
                : null;

        return SquadMemberDto.builder()
                .athleteId(athleteId)
                .athlete(athleteDto)
                .jerseyNumber(member.getJerseyNumber())
                .sector(member.getSector())
                .role(member.getRole())
                .order(member.getDisplayOrder())
                .build();
    }

    public static SquadStaffDto mapStaffMember(SquadStaffMember staff) {
        if (staff == null) return null;
        return SquadStaffDto.builder()
                .role(staff.getRole())
                .name(staff.getName())
                .build();
    }

    public static SquadDto mapSquad(Squad squad) {
        if (squad == null) return null;
        String idStr = squad.getId() != null ? squad.getId().toString() : null;

        List<SquadStaffDto> staffList = (squad.getStaff() != null ? squad.getStaff() : Collections.<SquadStaffMember>emptyList()).stream()
                .map(FootballMapperHelper::mapStaffMember)
                .toList();

        List<SquadMemberDto> memberList = (squad.getMembers() != null ? squad.getMembers() : Collections.<SquadMember>emptyList()).stream()
                .map(FootballMapperHelper::mapSquadMember)
                .toList();

        return SquadDto.builder()
                .id(idStr)
                .mongoId(idStr)
                .year(squad.getYear())
                .category(squad.getCategory())
                .seasonName(squad.getSeasonName())
                .coachName(squad.getCoachName())
                .staff(staffList)
                .coverUrl(squad.getCoverUrl())
                .isCurrent(squad.isCurrent())
                .members(memberList)
                .isActive(squad.isActive())
                .createdAt(squad.getCreatedAt())
                .updatedAt(squad.getUpdatedAt())
                .build();
    }

    public static SquadDto organizeSquadSectors(Squad squad) {
        SquadDto dto = mapSquad(squad);
        if (dto == null) return null;

        List<SquadMemberDto> allMembers = dto.getMembers() != null ? dto.getMembers() : Collections.emptyList();

        List<SquadMemberDto> goalkeepers = allMembers.stream().filter(m -> m.getSector() == SquadSector.GOALKEEPER).toList();
        List<SquadMemberDto> defenders = allMembers.stream().filter(m -> m.getSector() == SquadSector.DEFENDER).toList();
        List<SquadMemberDto> midfielders = allMembers.stream().filter(m -> m.getSector() == SquadSector.MIDFIELDER).toList();
        List<SquadMemberDto> forwards = allMembers.stream().filter(m -> m.getSector() == SquadSector.FORWARD).toList();

        SquadSectorsDto sectors = SquadSectorsDto.builder()
                .goalkeepers(new ArrayList<>(goalkeepers))
                .defenders(new ArrayList<>(defenders))
                .midfielders(new ArrayList<>(midfielders))
                .forwards(new ArrayList<>(forwards))
                .build();

        dto.setSectors(sectors);
        return dto;
    }
}

