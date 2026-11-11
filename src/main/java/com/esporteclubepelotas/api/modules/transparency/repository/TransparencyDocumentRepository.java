package com.esporteclubepelotas.api.modules.transparency.repository;

import com.esporteclubepelotas.api.modules.transparency.entity.TransparencyDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransparencyDocumentRepository extends JpaRepository<TransparencyDocument, UUID> {

    List<TransparencyDocument> findByPublishedTrueOrderByYearDescCreatedAtDesc();

    @Query("SELECT d FROM TransparencyDocument d WHERE d.published = true AND (:category IS NULL OR d.category = :category) AND (:year IS NULL OR d.year = :year) ORDER BY d.year DESC, d.createdAt DESC")
    List<TransparencyDocument> findPublishedWithFilters(@Param("category") String category, @Param("year") Integer year);

    List<TransparencyDocument> findAllByOrderByYearDescCreatedAtDesc();
}

