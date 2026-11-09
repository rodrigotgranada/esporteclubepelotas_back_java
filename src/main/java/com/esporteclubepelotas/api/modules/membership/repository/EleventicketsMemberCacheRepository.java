package com.esporteclubepelotas.api.modules.membership.repository;

import com.esporteclubepelotas.api.modules.membership.entity.EleventicketsMemberCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EleventicketsMemberCacheRepository extends JpaRepository<EleventicketsMemberCache, String> {

    Optional<EleventicketsMemberCache> findByCpf(String cpf);

    Optional<EleventicketsMemberCache> findByEleventicketsId(String eleventicketsId);

    boolean existsByCpf(String cpf);

    long countByStatus(String status);
}

