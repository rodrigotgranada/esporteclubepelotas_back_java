package com.esporteclubepelotas.api.modules.football.repository;

import com.esporteclubepelotas.api.modules.football.entity.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AthleteRepository extends JpaRepository<Athlete, UUID>, JpaSpecificationExecutor<Athlete> {

    Optional<Athlete> findBySlugAndIsActiveTrue(String slug);

    Optional<Athlete> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}

