package com.esporteclubepelotas.api.modules.timeline.repository;

import com.esporteclubepelotas.api.modules.timeline.entity.TimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TimelineEventRepository extends JpaRepository<TimelineEvent, UUID>, JpaSpecificationExecutor<TimelineEvent> {

    List<TimelineEvent> findByIsActiveTrueOrderByYearAscDisplayOrderAscCreatedAtAsc();
}
