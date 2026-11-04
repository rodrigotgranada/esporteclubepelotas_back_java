package com.esporteclubepelotas.api.modules.theme.repository;

import com.esporteclubepelotas.api.modules.theme.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {

    Optional<Theme> findByName(String name);

    boolean existsByName(String name);

    Optional<Theme> findFirstByIsDefaultTrue();

    Optional<Theme> findFirstByIsActiveTrueAndIsCampaignFalse();

    @Query("SELECT t FROM Theme t WHERE t.isCampaign = true AND t.isActive = true " +
           "AND (t.startDate IS NULL OR t.startDate <= :now) " +
           "AND (t.endDate IS NULL OR t.endDate >= :now) " +
           "ORDER BY t.startDate DESC, t.updatedAt DESC")
    List<Theme> findActiveCampaigns(@Param("now") Instant now);

    List<Theme> findAllByOrderByCreatedAtDesc();
}

