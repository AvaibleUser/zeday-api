package com.ayds.zeday.service.scheduling;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.zeday.domain.dto.availability.AddAvailabilityDto;
import com.ayds.zeday.domain.dto.availability.UpdateAvailabilityDto;
import com.ayds.zeday.domain.entity.AvailabilityEntity;
import com.ayds.zeday.domain.entity.ScheduleEntity;
import com.ayds.zeday.domain.exception.BadRequestException;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.AvailabilityRepository;
import com.ayds.zeday.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void addScheduleAvailability(long businessId, long scheduleId, AddAvailabilityDto availability) {
        ScheduleEntity schedule = scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el horario o la compañia"));

        if (availability.recurring()) {
            if (availabilityRepository.existsByScheduleIdAndRecurringAndDayOfWeek(scheduleId, true,
                    availability.dayOfWeek().orElseThrow(
                            () -> new BadRequestException(
                                    "La disponibilidad recurrente requiere un dia de la semana")))) {
                throw new RequestConflictException("La disponibilidad recurrente del dia de la semana esta ocupado");
            }
        } else {
            if (availabilityRepository.existsByScheduleIdAndRecurringAndSpecificDay(scheduleId, false,
                    availability.specificDay()
                            .orElseThrow(
                                    () -> new BadRequestException("La disponibilidad requiere un dia especifico")))) {
                throw new RequestConflictException("La disponibilidad de ese dia esta ocupado");
            }
        }

        AvailabilityEntity newAvailability = AvailabilityEntity.builder()
                .startAt(availability.startAt())
                .endAt(availability.endAt())
                .recurring(availability.recurring())
                .dayOfWeek(availability.dayOfWeek().filter(dow -> availability.recurring()).orElse(null))
                .specificDay(availability.specificDay().filter(sd -> !availability.recurring()).orElse(null))
                .schedule(schedule)
                .build();

        availabilityRepository.save(newAvailability);
    }

    @Transactional
    public void updateScheduleUnavailability(long businessId, long scheduleId, long availabilityId,
            UpdateAvailabilityDto availability) {
        AvailabilityEntity dbAvailability = availabilityRepository
                .findByIdAndScheduleIdAndScheduleBusinessId(availabilityId, scheduleId, businessId)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la indisponibilidad"));

        availability.startAt().ifPresent(dbAvailability::setStartAt);
        availability.endAt().ifPresent(dbAvailability::setEndAt);
        availability.dayOfWeek()
                .filter(sd -> dbAvailability.getRecurring())
                .ifPresent(dbAvailability::setDayOfWeek);

        availability.specificDay()
                .filter(sd -> !dbAvailability.getRecurring())
                .ifPresent(dbAvailability::setSpecificDay);

        availabilityRepository.save(dbAvailability);
    }

    @Transactional
    public void removeScheduleAvailability(long businessId, long scheduleId, long availabilityId) {
        scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el horario o la compañia"));

        availabilityRepository.deleteByIdAndScheduleIdAndScheduleBusinessId(availabilityId, scheduleId, businessId);
    }
}
