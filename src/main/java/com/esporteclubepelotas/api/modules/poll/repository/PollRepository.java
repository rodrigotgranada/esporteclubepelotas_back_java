package com.esporteclubepelotas.api.modules.poll.repository;

import com.esporteclubepelotas.api.modules.poll.entity.Poll;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {

    @Query("SELECT p.id FROM Poll p WHERE p.active = true ORDER BY p.createdAt DESC")
    List<UUID> findActivePollIds(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Poll p LEFT JOIN FETCH p.options WHERE p.id = :id")
    Optional<Poll> findByIdWithOptions(@Param("id") UUID id);

    default Optional<Poll> findFirstActive() {
        List<UUID> ids = findActivePollIds(PageRequest.of(0, 1));
        if (ids.isEmpty()) {
            return Optional.empty();
        }
        return findByIdWithOptions(ids.get(0));
    }

    @Query("SELECT DISTINCT p FROM Poll p LEFT JOIN FETCH p.options ORDER BY p.createdAt DESC")
    List<Poll> findAllByOrderByCreatedAtDesc();
}
