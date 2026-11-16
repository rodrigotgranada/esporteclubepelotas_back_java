package com.esporteclubepelotas.api.modules.kit.repository;

import com.esporteclubepelotas.api.modules.kit.entity.ClubKit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClubKitRepository extends JpaRepository<ClubKit, UUID> {

    @EntityGraph(attributePaths = {"championship"})
    Optional<ClubKit> findByIdAndDeletedAtIsNull(UUID id);

    @EntityGraph(attributePaths = {"championship"})
    List<ClubKit> findByDeletedAtIsNullOrderBySeasonYearDescDisplayOrderAscCreatedAtDesc();

    @EntityGraph(attributePaths = {"championship"})
    List<ClubKit> findByIsCurrentTrueAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAscCreatedAtAsc();

    @EntityGraph(attributePaths = {"championship"})
    List<ClubKit> findByIsCurrentFalseAndIsActiveTrueAndDeletedAtIsNullOrderBySeasonYearDescDisplayOrderAscCreatedAtDesc();

    @EntityGraph(attributePaths = {"championship"})
    List<ClubKit> findByCompetitionIgnoreCaseAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAscCreatedAtAsc(String competition);

    @EntityGraph(attributePaths = {"championship"})
    List<ClubKit> findByIsActiveTrueAndDeletedAtIsNullOrderBySeasonYearDescDisplayOrderAscCreatedAtDesc();

    @Query("SELECT DISTINCT k.competition, k.seasonYear FROM ClubKit k WHERE k.isActive = true AND k.deletedAt IS NULL AND k.isCurrent = false ORDER BY k.seasonYear DESC, k.competition ASC")
    List<Object[]> findHistoricalCompetitions();

    @Query("SELECT DISTINCT k.competition FROM ClubKit k WHERE k.deletedAt IS NULL ORDER BY k.competition ASC")
    List<String> findAllDistinctCompetitions();
}
