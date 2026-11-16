package com.esporteclubepelotas.api.modules.poll.repository;

import com.esporteclubepelotas.api.modules.poll.entity.Poll;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {

    @Query("SELECT DISTINCT p FROM Poll p LEFT JOIN FETCH p.options WHERE p.active = true ORDER BY p.createdAt DESC")
    List<Poll> findActivePolls(Pageable pageable);

    default Optional<Poll> findFirstActive() {
        List<Poll> list = findActivePolls(PageRequest.of(0, 1));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Query("SELECT DISTINCT p FROM Poll p LEFT JOIN FETCH p.options ORDER BY p.createdAt DESC")
    List<Poll> findAllByOrderByCreatedAtDesc();
}
