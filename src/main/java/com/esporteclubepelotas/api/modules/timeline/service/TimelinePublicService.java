package com.esporteclubepelotas.api.modules.timeline.service;

import com.esporteclubepelotas.api.modules.timeline.dto.GetTimelineResponseDto;
import com.esporteclubepelotas.api.modules.timeline.dto.TimelineEventDto;
import com.esporteclubepelotas.api.modules.timeline.helper.TimelineMapperHelper;
import com.esporteclubepelotas.api.modules.timeline.repository.TimelineEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimelinePublicService {

    private final TimelineEventRepository timelineEventRepository;

    @Transactional(readOnly = true)
    public GetTimelineResponseDto getPublicTimeline() {
        List<TimelineEventDto> dtos = timelineEventRepository.findByIsActiveTrueOrderByYearAscDisplayOrderAscCreatedAtAsc()
                .stream()
                .map(TimelineMapperHelper::map)
                .toList();

        return GetTimelineResponseDto.builder()
                .data(dtos)
                .total(dtos.size())
                .page(1)
                .limit(dtos.size())
                .totalPages(1)
                .build();
    }
}

