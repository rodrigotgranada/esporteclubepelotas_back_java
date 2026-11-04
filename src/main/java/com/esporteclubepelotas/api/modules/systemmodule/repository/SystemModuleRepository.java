package com.esporteclubepelotas.api.modules.systemmodule.repository;

import com.esporteclubepelotas.api.modules.systemmodule.entity.SystemModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemModuleRepository extends JpaRepository<SystemModule, String> {
    Optional<SystemModule> findBySlug(String slug);
    List<SystemModule> findAllByOrderByCreatedAtAsc();
}
