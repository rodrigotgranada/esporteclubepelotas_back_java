package com.esporteclubepelotas.api.modules.anthem.repository;

import com.esporteclubepelotas.api.modules.anthem.entity.ClubAnthem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClubAnthemRepository extends JpaRepository<ClubAnthem, UUID> {

    Optional<ClubAnthem> findFirstByIsOfficialTrueAndDeletedAtIsNullAndIsActiveTrue();

    Optional<ClubAnthem> findFirstByDeletedAtIsNullAndIsActiveTrueOrderByDisplayOrderAscCreatedAtDesc();

    Optional<ClubAnthem> findByIdAndDeletedAtIsNull(UUID id);

    List<ClubAnthem> findByDeletedAtIsNullOrderByDisplayOrderAscCreatedAtDesc();

    List<ClubAnthem> findByDeletedAtIsNullAndIsActiveTrueOrderByDisplayOrderAscCreatedAtDesc();

    @Modifying
    @Query("UPDATE ClubAnthem a SET a.isOfficial = false WHERE a.id != :anthemId AND a.deletedAt IS NULL")
    void unsetOtherOfficialAnthems(@Param("anthemId") UUID anthemId);

    @Modifying
    @Query("UPDATE ClubAnthem a SET a.isOfficial = false WHERE a.deletedAt IS NULL")
    void unsetAllOfficialAnthems();
}
