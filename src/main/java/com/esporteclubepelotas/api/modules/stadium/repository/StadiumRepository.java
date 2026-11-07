package com.esporteclubepelotas.api.modules.stadium.repository;

import com.esporteclubepelotas.api.modules.stadium.entity.StadiumInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StadiumRepository extends JpaRepository<StadiumInfo, Long> {
}

