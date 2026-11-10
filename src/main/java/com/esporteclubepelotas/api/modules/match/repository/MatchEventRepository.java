package com.esporteclubepelotas.api.modules.match.repository;

import com.esporteclubepelotas.api.modules.match.entity.MatchEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatchEventRepository extends JpaRepository<MatchEvent, UUID> {

    @Query("SELECT e FROM MatchEvent e WHERE e.match.id = :matchId ORDER BY e.minute DESC, e.minuteExtra DESC NULLS LAST, e.createdAt DESC")
    List<MatchEvent> findByMatchIdOrderByTimelineDesc(@Param("matchId") UUID matchId);

    @Query("SELECT e FROM MatchEvent e WHERE e.match.id = :matchId ORDER BY e.minute ASC, e.minuteExtra ASC NULLS FIRST, e.createdAt ASC")
    List<MatchEvent> findByMatchIdOrderByTimelineAsc(@Param("matchId") UUID matchId);

    void deleteByMatchId(UUID matchId);
}

