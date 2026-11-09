package com.esporteclubepelotas.api.modules.symbol.repository;

import com.esporteclubepelotas.api.modules.symbol.entity.ClubSymbolOverview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubSymbolOverviewRepository extends JpaRepository<ClubSymbolOverview, String> {
}
