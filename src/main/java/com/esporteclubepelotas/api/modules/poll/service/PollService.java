package com.esporteclubepelotas.api.modules.poll.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.common.exception.ResourceNotFoundException;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.common.websocket.PortalWebSocketHandler;
import com.esporteclubepelotas.api.modules.poll.dto.*;
import com.esporteclubepelotas.api.modules.poll.entity.Poll;
import com.esporteclubepelotas.api.modules.poll.entity.PollOption;
import com.esporteclubepelotas.api.modules.poll.entity.PollVote;
import com.esporteclubepelotas.api.modules.poll.repository.PollOptionRepository;
import com.esporteclubepelotas.api.modules.poll.repository.PollRepository;
import com.esporteclubepelotas.api.modules.poll.repository.PollVoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollVoteRepository pollVoteRepository;
    private final PortalWebSocketHandler webSocketHandler;

    // --- PÚBLICO / TORCEDOR ---

    @Transactional(readOnly = true)
    public Optional<PollDto> getActivePoll(UserPrincipal principal) {
        return pollRepository.findFirstActive()
                .map(poll -> toDto(poll, principal));
    }

    @Transactional(readOnly = true)
    public PollDto getPollById(UUID id, UserPrincipal principal) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada: " + id));
        return toDto(poll, principal);
    }

    @Transactional
    public PollDto vote(UUID pollId, VotePollDto dto, UserPrincipal principal) {
        if (principal == null) {
            throw new BusinessException("É necessário estar autenticado para votar.");
        }

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada: " + pollId));

        if (!poll.isActive()) {
            throw new BusinessException("Esta enquete já foi encerrada.");
        }

        Instant now = Instant.now();
        if (poll.getStartsAt() != null && now.isBefore(poll.getStartsAt())) {
            throw new BusinessException("Esta enquete ainda não foi iniciada.");
        }
        if (poll.getEndsAt() != null && now.isAfter(poll.getEndsAt())) {
            throw new BusinessException("O prazo de votação desta enquete já encerrou.");
        }

        // REGRA DE OURO: SOMENTE SÓCIOS (Role SOCIO, OWNER ou ADMIN)
        if (poll.isOnlyMembers()) {
            String role = principal.getRole() != null ? principal.getRole().toUpperCase() : "";
            boolean isSocioOrStaff = role.contains("SOCIO") || role.contains("ADMIN") || role.contains("OWNER");
            if (!isSocioOrStaff) {
                throw new BusinessException("A votação nesta enquete é exclusiva para sócios adimplentes do Esporte Clube Pelotas.");
            }
        }

        UUID userId;
        try {
            userId = UUID.fromString(principal.getId());
        } catch (Exception e) {
            throw new BusinessException("Identificador de usuário inválido.");
        }

        if (pollVoteRepository.existsByPollIdAndUserId(pollId, userId)) {
            throw new BusinessException("Você já registrou o seu voto nesta enquete.");
        }

        PollOption option = pollOptionRepository.findById(dto.getOptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Opção de voto não encontrada: " + dto.getOptionId()));

        if (!option.getPoll().getId().equals(pollId)) {
            throw new BusinessException("Opção não pertence a esta enquete.");
        }

        // Registrar voto
        PollVote vote = PollVote.builder()
                .pollId(pollId)
                .optionId(option.getId())
                .userId(userId)
                .userRole(principal.getRole() != null ? principal.getRole() : "SOCIO")
                .build();
        pollVoteRepository.save(vote);

        // Incrementar contador
        pollOptionRepository.incrementVoteCount(option.getId());

        // Notificar via WebSocket
        webSocketHandler.broadcastAfterCommit("polls", "VOTE_CAST", pollId.toString());

        // Recarregar enquete atualizada
        return getPollById(pollId, principal);
    }

    // --- ADMINISTRATIVO ---

    @Transactional(readOnly = true)
    public List<PollDto> listAllAdmin() {
        return pollRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(p -> toDto(p, null)).toList();
    }

    @Transactional
    public PollDto create(CreatePollDto dto) {
        Poll poll = Poll.builder()
                .title(dto.getTitle().trim())
                .description(dto.getDescription())
                .onlyMembers(dto.isOnlyMembers())
                .active(dto.isActive())
                .startsAt(dto.getStartsAt())
                .endsAt(dto.getEndsAt())
                .options(new ArrayList<>())
                .build();

        int order = 1;
        for (String optTitle : dto.getOptions()) {
            if (optTitle != null && !optTitle.isBlank()) {
                PollOption opt = PollOption.builder()
                        .poll(poll)
                        .title(optTitle.trim())
                        .votesCount(0)
                        .displayOrder(order++)
                        .build();
                poll.getOptions().add(opt);
            }
        }

        poll = pollRepository.save(poll);
        webSocketHandler.broadcastAfterCommit("polls", "POLL_CREATED", poll.getId().toString());
        return toDto(poll, null);
    }

    @Transactional
    public PollDto update(UUID id, UpdatePollDto dto) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada: " + id));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            poll.setTitle(dto.getTitle().trim());
        }
        if (dto.getDescription() != null) {
            poll.setDescription(dto.getDescription());
        }
        if (dto.getOnlyMembers() != null) {
            poll.setOnlyMembers(dto.getOnlyMembers());
        }
        if (dto.getActive() != null) {
            poll.setActive(dto.getActive());
        }
        if (dto.getStartsAt() != null) {
            poll.setStartsAt(dto.getStartsAt());
        }
        if (dto.getEndsAt() != null) {
            poll.setEndsAt(dto.getEndsAt());
        }

        poll = pollRepository.save(poll);
        webSocketHandler.broadcastAfterCommit("polls", "POLL_UPDATED", poll.getId().toString());
        return toDto(poll, null);
    }

    @Transactional
    public PollDto toggleActive(UUID id) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada: " + id));

        poll.setActive(!poll.isActive());
        poll = pollRepository.save(poll);
        webSocketHandler.broadcastAfterCommit("polls", "POLL_TOGGLED", poll.getId().toString());
        return toDto(poll, null);
    }

    @Transactional
    public void delete(UUID id) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada: " + id));
        pollRepository.delete(poll);
        webSocketHandler.broadcastAfterCommit("polls", "POLL_DELETED", id.toString());
    }

    // --- CONVERSOR AUXILIAR ---

    private PollDto toDto(Poll poll, UserPrincipal principal) {
        int totalVotes = 0;
        if (poll.getOptions() != null) {
            for (PollOption opt : poll.getOptions()) {
                totalVotes += (opt.getVotesCount() != null ? opt.getVotesCount() : 0);
            }
        }

        final int finalTotal = totalVotes;
        List<PollOptionDto> optionDtos = new ArrayList<>();
        if (poll.getOptions() != null) {
            for (PollOption opt : poll.getOptions()) {
                int votes = opt.getVotesCount() != null ? opt.getVotesCount() : 0;
                double pct = finalTotal > 0 ? ((double) votes / finalTotal) * 100.0 : 0.0;
                // format 1 decimal place
                pct = Math.round(pct * 10.0) / 10.0;

                optionDtos.add(PollOptionDto.builder()
                        .id(opt.getId())
                        .title(opt.getTitle())
                        .votesCount(votes)
                        .displayOrder(opt.getDisplayOrder())
                        .percentage(pct)
                        .build());
            }
        }

        boolean hasVoted = false;
        UUID votedOptionId = null;
        if (principal != null) {
            try {
                UUID userId = UUID.fromString(principal.getId());
                Optional<PollVote> voteOpt = pollVoteRepository.findByPollIdAndUserId(poll.getId(), userId);
                if (voteOpt.isPresent()) {
                    hasVoted = true;
                    votedOptionId = voteOpt.get().getOptionId();
                }
            } catch (Exception ignored) {}
        }

        return PollDto.builder()
                .id(poll.getId())
                .title(poll.getTitle())
                .description(poll.getDescription())
                .onlyMembers(poll.isOnlyMembers())
                .active(poll.isActive())
                .startsAt(poll.getStartsAt())
                .endsAt(poll.getEndsAt())
                .totalVotes(totalVotes)
                .userHasVoted(hasVoted)
                .userVotedOptionId(votedOptionId)
                .options(optionDtos)
                .createdAt(poll.getCreatedAt())
                .updatedAt(poll.getUpdatedAt())
                .build();
    }
}

