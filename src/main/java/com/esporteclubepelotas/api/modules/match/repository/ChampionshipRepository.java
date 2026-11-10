package com.esporteclubepelotas.api.modules.match.repository;

import com.esporteclubepelotas.api.modules.match.entity.Championship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChampionshipRepository extends JpaRepository<Championship, UUID> {

    @Query("SELECT c FROM Championship c WHERE c.deletedAt IS NULL ORDER BY c.displayOrder ASC, c.seasonYear DESC, c.name ASC")
    List<Championship> findAllActive();

    @Query("SELECT c FROM Championship c WHERE c.deletedAt IS NULL AND c.isActive = true ORDER BY c.displayOrder ASC, c.seasonYear DESC, c.name ASC")
    List<Championship> findCurrentActive();

    @Query("SELECT c FROM Championship c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Championship> findActiveById(UUID id);
}

