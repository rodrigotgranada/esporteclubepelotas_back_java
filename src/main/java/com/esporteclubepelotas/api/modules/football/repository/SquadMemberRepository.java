package com.esporteclubepelotas.api.modules.football.repository;

import com.esporteclubepelotas.api.modules.football.entity.SquadMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SquadMemberRepository extends JpaRepository<SquadMember, UUID> {

    List<SquadMember> findBySquadIdOrderByDisplayOrderAsc(UUID squadId);

    @Query("SELECT m FROM SquadMember m JOIN FETCH m.squad s WHERE m.athlete.id = :athleteId AND s.isActive = true ORDER BY s.year DESC")
    List<SquadMember> findSeasonsByAthleteId(@Param("athleteId") UUID athleteId);

    Optional<SquadMember> findBySquadIdAndAthleteId(UUID squadId, UUID athleteId);

    @Modifying
    @Query("DELETE FROM SquadMember m WHERE m.squad.id = :squadId")
    void deleteBySquadId(@Param("squadId") UUID squadId);

    @Modifying
    @Query("DELETE FROM SquadMember m WHERE m.squad.id = :squadId AND m.athlete.id = :athleteId")
    void deleteBySquadIdAndAthleteId(@Param("squadId") UUID squadId, @Param("athleteId") UUID athleteId);
}

