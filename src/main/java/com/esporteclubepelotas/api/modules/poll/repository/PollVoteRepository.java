package com.esporteclubepelotas.api.modules.poll.repository;

import com.esporteclubepelotas.api.modules.poll.entity.PollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PollVoteRepository extends JpaRepository<PollVote, UUID> {

    boolean existsByPollIdAndUserId(UUID pollId, UUID userId);

    Optional<PollVote> findByPollIdAndUserId(UUID pollId, UUID userId);

    long countByPollId(UUID pollId);
}

