package com.esporteclubepelotas.api.modules.football.repository;

import com.esporteclubepelotas.api.modules.football.entity.Athlete;
import com.esporteclubepelotas.api.modules.football.entity.AthleteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AthleteRepository extends JpaRepository<Athlete, UUID>, JpaSpecificationExecutor<Athlete> {

    Optional<Athlete> findBySlugAndIsActiveTrue(String slug);

    Optional<Athlete> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    @Query("SELECT a FROM Athlete a WHERE LOWER(a.name) = LOWER(:term) OR LOWER(a.nickname) = LOWER(:term)")
    List<Athlete> findByNameOrNicknameIgnoreCase(@Param("term") String term);

    @Query("SELECT a FROM Athlete a WHERE a.athleteType = :athleteType AND LOWER(a.name) = LOWER(:name)")
    List<Athlete> findByAthleteTypeAndNameIgnoreCase(@Param("athleteType") AthleteType athleteType, @Param("name") String name);
}
