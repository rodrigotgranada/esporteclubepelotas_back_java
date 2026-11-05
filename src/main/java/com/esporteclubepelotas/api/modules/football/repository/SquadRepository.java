package com.esporteclubepelotas.api.modules.football.repository;

import com.esporteclubepelotas.api.modules.football.entity.Squad;
import com.esporteclubepelotas.api.modules.football.entity.SquadCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SquadRepository extends JpaRepository<Squad, UUID>, JpaSpecificationExecutor<Squad> {

    @Query("SELECT DISTINCT s.year FROM Squad s WHERE s.isActive = true ORDER BY s.year DESC")
    List<Integer> findDistinctYears();

    @Query("SELECT DISTINCT s.category FROM Squad s WHERE s.isActive = true")
    List<SquadCategory> findDistinctCategories();

    @Query("SELECT s.year, s.category FROM Squad s WHERE s.isActive = true ORDER BY s.year DESC")
    List<Object[]> findActiveYearAndCategories();

    Optional<Squad> findFirstByCategoryAndIsCurrentTrueAndIsActiveTrue(SquadCategory category);

    Optional<Squad> findFirstByCategoryAndIsActiveTrueOrderByYearDescCreatedAtDesc(SquadCategory category);

    Optional<Squad> findByYearAndCategoryAndIsActiveTrue(Integer year, SquadCategory category);

    @Modifying
    @Query("UPDATE Squad s SET s.isCurrent = false WHERE s.category = :category AND s.id != :exceptId")
    void clearOtherCurrentSquads(@Param("category") SquadCategory category, @Param("exceptId") UUID exceptId);

    @Modifying
    @Query("UPDATE Squad s SET s.isCurrent = false WHERE s.category = :category")
    void clearAllCurrentSquads(@Param("category") SquadCategory category);
}
