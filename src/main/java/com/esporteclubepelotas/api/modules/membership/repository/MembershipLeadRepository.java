package com.esporteclubepelotas.api.modules.membership.repository;

import com.esporteclubepelotas.api.modules.membership.entity.MembershipLead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MembershipLeadRepository extends JpaRepository<MembershipLead, UUID>, JpaSpecificationExecutor<MembershipLead> {

    @Query("""
        SELECT l FROM MembershipLead l
        JOIN FETCH l.plan p
        ORDER BY l.createdAt DESC
    """)
    List<MembershipLead> findAllWithPlan();

    @Query("""
        SELECT l FROM MembershipLead l
        JOIN FETCH l.plan p
        WHERE (:status IS NULL OR l.status = :status)
        ORDER BY l.createdAt DESC
    """)
    Page<MembershipLead> findByStatusWithPlan(@Param("status") String status, Pageable pageable);

    long countByStatus(String status);
}
