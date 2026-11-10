package com.esporteclubepelotas.api.modules.match.repository;

import com.esporteclubepelotas.api.modules.match.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    @Query("SELECT m FROM Match m JOIN FETCH m.championship c WHERE m.deletedAt IS NULL ORDER BY m.matchDate DESC")
    List<Match> findAllActive();

    @Query("SELECT m FROM Match m JOIN FETCH m.championship c WHERE m.championship.id = :championshipId AND m.deletedAt IS NULL ORDER BY m.matchDate ASC")
    List<Match> findByChampionshipId(@Param("championshipId") UUID championshipId);

    @Query("SELECT m FROM Match m JOIN FETCH m.championship c WHERE m.id = :id AND m.deletedAt IS NULL")
    Optional<Match> findActiveById(@Param("id") UUID id);

    /**
     * Busca o próximo jogo do Pelotas (partida em andamento ou próxima agendada)
     * Regra: somente se o campeonato estiver ativo!
     */
    @Query(value = "SELECT m FROM Match m JOIN FETCH m.championship c " +
           "WHERE m.deletedAt IS NULL " +
           "AND c.deletedAt IS NULL " +
           "AND c.isActive = true " +
           "AND (m.status = com.esporteclubepelotas.api.modules.match.entity.MatchStatus.IN_PROGRESS " +
           "     OR (m.status = com.esporteclubepelotas.api.modules.match.entity.MatchStatus.SCHEDULED AND m.matchDate >= :now)) " +
           "ORDER BY CASE WHEN m.status = com.esporteclubepelotas.api.modules.match.entity.MatchStatus.IN_PROGRESS THEN 0 ELSE 1 END, m.matchDate ASC " +
           "LIMIT 1")
    Optional<Match> findNextMatch(@Param("now") Instant now);
}

