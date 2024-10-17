package com.ayds.zeday.service.scheduling;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.zeday.domain.dto.unavailability.AddUnavailabilityDto;
import com.ayds.zeday.domain.dto.unavailability.UpdateUnavailabilityDto;
import com.ayds.zeday.domain.entity.ScheduleEntity;
import com.ayds.zeday.domain.entity.UnavailabilityEntity;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.ScheduleRepository;
import com.ayds.zeday.repository.UnavailabilityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnavailabilityService {

    private final UnavailabilityRepository unavailabilityRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void addScheduleUnavailability(long businessId, long scheduleId, AddUnavailabilityDto unavailability) {
        ScheduleEntity schedule = scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el horario o la compañia"));

        if (unavailabilityRepository.existsByScheduleIdAndSpecificDay(scheduleId, unavailability.specificDay())) {
            throw new RequestConflictException("Ese dia en especifico ya tiene una indisponibilidad");
        }

        UnavailabilityEntity newUnavailability = UnavailabilityEntity.builder()
                .startAt(unavailability.startAt())
                .endAt(unavailability.endAt())
                .specificDay(unavailability.specificDay())
                .schedule(schedule)
                .build();

        unavailabilityRepository.save(newUnavailability);
    }

    @Transactional
    public void updateScheduleUnavailability(long businessId, long scheduleId, long unavailabilityId,
            UpdateUnavailabilityDto unavailability) {
        UnavailabilityEntity dbUnavailability = unavailabilityRepository
                .findByIdAndScheduleIdAndScheduleBusinessId(unavailabilityId, scheduleId, businessId)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la indisponibilidad"));

        unavailability.startAt().ifPresent(dbUnavailability::setStartAt);
        unavailability.endAt().ifPresent(dbUnavailability::setEndAt);
        unavailability.specificDay().ifPresent(dbUnavailability::setSpecificDay);

        unavailabilityRepository.save(dbUnavailability);
    }

    @Transactional
    public void removeScheduleUnavailability(long businessId, long scheduleId, long unavailabilityId) {
        scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el horario o la compañia"));

        unavailabilityRepository.deleteByIdAndScheduleIdAndScheduleBusinessId(unavailabilityId, scheduleId, businessId);
    }
}
