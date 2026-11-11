package com.esporteclubepelotas.api.modules.poll.repository;

import com.esporteclubepelotas.api.modules.poll.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PollRepository extends JpaRepository<Poll, UUID> {

    @Query("SELECT p FROM Poll p WHERE p.active = true ORDER BY p.createdAt DESC")
    List<Poll> findActivePolls();

    default Optional<Poll> findFirstActive() {
        List<Poll> list = findActivePolls();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    List<Poll> findAllByOrderByCreatedAtDesc();
}

