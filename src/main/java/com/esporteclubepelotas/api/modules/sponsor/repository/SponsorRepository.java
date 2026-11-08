package com.esporteclubepelotas.api.modules.sponsor.repository;

import com.esporteclubepelotas.api.modules.sponsor.entity.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, UUID>, JpaSpecificationExecutor<Sponsor> {

    @Query("""
        SELECT s FROM Sponsor s
        WHERE s.active = true
          AND s.deletedAt IS NULL
          AND (s.contractStartDate IS NULL OR s.contractStartDate <= :today)
          AND (s.contractEndDate IS NULL OR s.contractEndDate >= :today)
        ORDER BY s.featured DESC, s.displayOrder ASC, s.name ASC
    """)
    List<Sponsor> findActivePublicSponsors(@Param("today") LocalDate today);

    @Query("""
        SELECT s FROM Sponsor s
        WHERE s.active = true
          AND s.featured = true
          AND s.deletedAt IS NULL
          AND (s.contractStartDate IS NULL OR s.contractStartDate <= :today)
          AND (s.contractEndDate IS NULL OR s.contractEndDate >= :today)
        ORDER BY s.displayOrder ASC, s.name ASC
    """)
    List<Sponsor> findFeaturedPublicSponsors(@Param("today") LocalDate today);

    @Modifying
    @Query("UPDATE Sponsor s SET s.clicksCount = s.clicksCount + 1 WHERE s.id = :id AND s.deletedAt IS NULL")
    void incrementClicksCount(@Param("id") UUID id);
}
