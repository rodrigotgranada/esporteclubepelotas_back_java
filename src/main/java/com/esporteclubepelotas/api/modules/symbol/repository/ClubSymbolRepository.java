package com.esporteclubepelotas.api.modules.symbol.repository;

import com.esporteclubepelotas.api.modules.symbol.entity.ClubSymbol;
import com.esporteclubepelotas.api.modules.symbol.entity.SymbolCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClubSymbolRepository extends JpaRepository<ClubSymbol, UUID> {

    Optional<ClubSymbol> findByIdAndDeletedAtIsNull(UUID id);

    List<ClubSymbol> findByDeletedAtIsNullOrderByCategoryAscDisplayOrderAscYearStartAscCreatedAtDesc();

    List<ClubSymbol> findByCategoryAndDeletedAtIsNullOrderByDisplayOrderAscYearStartAscCreatedAtDesc(SymbolCategory category);

    List<ClubSymbol> findByIsActiveTrueAndDeletedAtIsNullOrderByCategoryAscDisplayOrderAscYearStartAscCreatedAtDesc();

    List<ClubSymbol> findByCategoryAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAscYearStartAscCreatedAtDesc(SymbolCategory category);

    Optional<ClubSymbol> findFirstByCategoryAndIsOfficialTrueAndDeletedAtIsNullAndIsActiveTrue(SymbolCategory category);

    @Modifying
    @Query("UPDATE ClubSymbol s SET s.isOfficial = false WHERE s.category = :category AND s.id <> :id")
    void unsetOtherOfficialSymbols(SymbolCategory category, UUID id);
}

