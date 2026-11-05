package com.esporteclubepelotas.api.modules.idol.helper;

import com.esporteclubepelotas.api.modules.idol.entity.Idol;
import com.esporteclubepelotas.api.modules.idol.entity.IdolCategory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class IdolSpecifications {

    private IdolSpecifications() {
    }

    public static Specification<Idol> withFilters(
            String search,
            IdolCategory category,
            Boolean isFeatured,
            boolean includeInactive
    ) {
        final String searchClean = (search != null && !search.isBlank()) ? search.trim().toLowerCase() : null;
        final IdolCategory targetCategory = category;
        final Boolean targetFeatured = isFeatured;
        final boolean targetIncludeInactive = includeInactive;

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!targetIncludeInactive) {
                predicates.add(cb.isTrue(root.get("isActive")));
            }

            if (targetCategory != null) {
                predicates.add(cb.equal(root.get("category"), targetCategory));
            }

            if (targetFeatured != null) {
                predicates.add(cb.equal(root.get("isFeatured"), targetFeatured));
            }

            if (searchClean != null) {
                String pattern = "%" + searchClean + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("nickname")), pattern),
                        cb.like(cb.lower(root.get("quote")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

