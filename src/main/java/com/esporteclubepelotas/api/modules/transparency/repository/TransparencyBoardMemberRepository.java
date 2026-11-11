package com.esporteclubepelotas.api.modules.transparency.repository;

import com.esporteclubepelotas.api.modules.transparency.entity.TransparencyBoardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransparencyBoardMemberRepository extends JpaRepository<TransparencyBoardMember, UUID> {

    List<TransparencyBoardMember> findByActiveTrueOrderByDisplayOrderAscNameAsc();

    List<TransparencyBoardMember> findByActiveTrueAndBoardTypeOrderByDisplayOrderAscNameAsc(String boardType);

    List<TransparencyBoardMember> findAllByOrderByDisplayOrderAscNameAsc();
}

