package com.esporteclubepelotas.api.modules.membership.repository;

import com.esporteclubepelotas.api.modules.membership.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, UUID>, JpaSpecificationExecutor<MembershipPlan> {

    @Query("""
        SELECT p FROM MembershipPlan p
        WHERE p.active = true
          AND p.deletedAt IS NULL
        ORDER BY p.displayOrder ASC, p.price ASC
    """)
    List<MembershipPlan> findAllActivePlans();

    @Query("""
        SELECT p FROM MembershipPlan p
        WHERE p.deletedAt IS NULL
        ORDER BY p.displayOrder ASC, p.createdAt DESC
    """)
    List<MembershipPlan> findAllAdminPlans();

    @Query("""
        SELECT p FROM MembershipPlan p
        WHERE p.slug = :slug
          AND p.deletedAt IS NULL
    """)
    Optional<MembershipPlan> findBySlug(@Param("slug") String slug);

    @Query("""
        SELECT p FROM MembershipPlan p
        WHERE p.id = :id
          AND p.deletedAt IS NULL
    """)
    Optional<MembershipPlan> findActiveById(@Param("id") UUID id);

    boolean existsBySlugAndIdNot(String slug, UUID id);
    boolean existsBySlug(String slug);
}
