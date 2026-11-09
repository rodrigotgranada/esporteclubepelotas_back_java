package com.esporteclubepelotas.api.modules.kit.repository;

import com.esporteclubepelotas.api.modules.kit.entity.ClubKit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClubKitRepository extends JpaRepository<ClubKit, UUID> {

    Optional<ClubKit> findByIdAndDeletedAtIsNull(UUID id);

    List<ClubKit> findByDeletedAtIsNullOrderBySeasonYearDescDisplayOrderAscCreatedAtDesc();

    List<ClubKit> findByIsCurrentTrueAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAscCreatedAtAsc();

    List<ClubKit> findByIsCurrentFalseAndIsActiveTrueAndDeletedAtIsNullOrderBySeasonYearDescDisplayOrderAscCreatedAtDesc();

    List<ClubKit> findByCompetitionIgnoreCaseAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAscCreatedAtAsc(String competition);

    List<ClubKit> findByIsActiveTrueAndDeletedAtIsNullOrderBySeasonYearDescDisplayOrderAscCreatedAtDesc();

    @Query("SELECT DISTINCT k.competition, k.seasonYear FROM ClubKit k WHERE k.isActive = true AND k.deletedAt IS NULL AND k.isCurrent = false ORDER BY k.seasonYear DESC, k.competition ASC")
    List<Object[]> findHistoricalCompetitions();

    @Query("SELECT DISTINCT k.competition FROM ClubKit k WHERE k.deletedAt IS NULL ORDER BY k.competition ASC")
    List<String> findAllDistinctCompetitions();
}

