package com.esporteclubepelotas.api.modules.opponent.repository;

import com.esporteclubepelotas.api.modules.opponent.entity.OpponentClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpponentClubRepository extends JpaRepository<OpponentClub, UUID> {

    @Query("SELECT c FROM OpponentClub c WHERE c.deletedAt IS NULL ORDER BY c.displayOrder ASC, c.name ASC")
    List<OpponentClub> findAllActive();

    @Query("SELECT c FROM OpponentClub c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<OpponentClub> findActiveById(@Param("id") UUID id);

    @Query("SELECT c FROM OpponentClub c WHERE c.deletedAt IS NULL AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.shortName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.city) LIKE LOWER(CONCAT('%', :query, '%'))) ORDER BY c.displayOrder ASC, c.name ASC")
    List<OpponentClub> searchClubs(@Param("query") String query);
}
