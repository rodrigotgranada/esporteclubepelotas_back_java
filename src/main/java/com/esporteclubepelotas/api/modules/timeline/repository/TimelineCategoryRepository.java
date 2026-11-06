package com.esporteclubepelotas.api.modules.timeline.repository;

import com.esporteclubepelotas.api.modules.timeline.entity.TimelineCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimelineCategoryRepository extends JpaRepository<TimelineCategory, UUID> {

    Optional<TimelineCategory> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    List<TimelineCategory> findByIsActiveTrueOrderByDisplayOrderAscNameAsc();

    List<TimelineCategory> findAllByOrderByDisplayOrderAscNameAsc();

    @Query("SELECT COUNT(e) FROM TimelineEvent e WHERE e.category.id = :categoryId AND e.isActive = true")
    long countEventsByCategoryId(UUID categoryId);
}

