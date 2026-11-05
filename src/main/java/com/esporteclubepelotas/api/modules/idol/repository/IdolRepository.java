package com.esporteclubepelotas.api.modules.idol.repository;

import com.esporteclubepelotas.api.modules.idol.entity.Idol;
import com.esporteclubepelotas.api.modules.idol.entity.IdolCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IdolRepository extends JpaRepository<Idol, String>, JpaSpecificationExecutor<Idol> {

    Optional<Idol> findBySlug(String slug);

    Optional<Idol> findBySlugAndIsActiveTrue(String slug);

    List<Idol> findByIsActiveTrueAndIsFeaturedTrueOrderByDisplayOrderAscCreatedAtDesc(Pageable pageable);

    @Query("SELECT i FROM Idol i WHERE " +
            "(:includeInactive = true OR i.isActive = true) AND " +
            "(:category IS NULL OR i.category = :category) AND " +
            "(:isFeatured IS NULL OR i.isFeatured = :isFeatured) AND " +
            "(:search IS NULL OR " +
            "LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.nickname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.quote) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Idol> findWithFilters(
            @Param("search") String search,
            @Param("category") IdolCategory category,
            @Param("isFeatured") Boolean isFeatured,
            @Param("includeInactive") boolean includeInactive,
            Pageable pageable
    );

    @Query("SELECT i FROM Idol i WHERE i.isActive = true AND (" +
            "i.displayOrder < :order OR " +
            "(i.displayOrder = :order AND i.startYear < :startYear) OR " +
            "(i.displayOrder = :order AND i.startYear = :startYear AND i.id < :id)) " +
            "ORDER BY i.displayOrder DESC, i.startYear DESC, i.id DESC")
    List<Idol> findPreviousIdol(
            @Param("order") int order,
            @Param("startYear") int startYear,
            @Param("id") String id,
            Pageable pageable
    );

    @Query("SELECT i FROM Idol i WHERE i.isActive = true AND (" +
            "i.displayOrder > :order OR " +
            "(i.displayOrder = :order AND i.startYear > :startYear) OR " +
            "(i.displayOrder = :order AND i.startYear = :startYear AND i.id > :id)) " +
            "ORDER BY i.displayOrder ASC, i.startYear ASC, i.id ASC")
    List<Idol> findNextIdol(
            @Param("order") int order,
            @Param("startYear") int startYear,
            @Param("id") String id,
            Pageable pageable
    );
}

