package com.esporteclubepelotas.api.modules.news.repository;

import com.esporteclubepelotas.api.modules.news.entity.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsCategoryRepository extends JpaRepository<NewsCategory, UUID> {

    List<NewsCategory> findByIsActiveTrueOrderByDisplayOrderAscNameAsc();

    Optional<NewsCategory> findBySlug(String slug);

    Optional<NewsCategory> findBySlugAndIsActiveTrue(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}

