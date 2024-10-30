package com.ayds.zeday.service.scheduling;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.zeday.domain.dto.availability.AddAvailabilityDto;
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

        DayOfWeek dow = DayOfWeek.of(availability.dayOfWeek());

        if (availabilityRepository.existsByScheduleIdAndRecurringAndDayOfWeek(scheduleId, true, dow)) {
            throw new RequestConflictException("La disponibilidad recurrente del dia de la semana esta ocupado");
        }

        AvailabilityEntity newAvailability = availabilityRepository
                .findByScheduleIdAndScheduleBusinessId(scheduleId, businessId, AvailabilityEntity.class)
                .stream()
                .findFirst()
                .map(ava -> ava.withDayOfWeek(dow))
                .orElseGet(() -> AvailabilityEntity.builder()
                        .startAt(availability.startAt().orElseThrow(
                                () -> new BadRequestException("La disponibilidad debe tener una hora de inicio")))
                        .endAt(availability.endAt().orElseThrow(
                                () -> new BadRequestException("La disponibilidad debe tener una hora de fin")))
                        .recurring(true)
                        .dayOfWeek(dow)
                        .schedule(schedule)
                        .build());

        availabilityRepository.save(newAvailability);
    }

    @Transactional
    public void updateScheduleUnavailability(long businessId, long scheduleId,
            List<AddAvailabilityDto> availabilities) {
        ScheduleEntity schedule = scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el horario o la compañia"));

        List<AvailabilityEntity> dbAvailabilities = availabilityRepository
                .findByScheduleIdAndScheduleBusinessId(scheduleId, businessId, AvailabilityEntity.class);

        List<DayOfWeek> week = availabilities.stream()
                .map(AddAvailabilityDto::dayOfWeek)
                .map(DayOfWeek::of)
                .toList();

        LocalTime startAt = availabilities.getFirst()
                .startAt()
                .orElseGet(() -> dbAvailabilities.getFirst().getStartAt());

        LocalTime endAt = availabilities.getFirst()
                .endAt()
                .orElseGet(() -> dbAvailabilities.getFirst().getEndAt());

        List<AvailabilityEntity> newAvailabilities = week.stream()
                .map(dow -> AvailabilityEntity.builder()
                        .startAt(startAt)
                        .endAt(endAt)
                        .recurring(true)
                        .dayOfWeek(dow)
                        .schedule(schedule)
                        .build())
                .toList();
        availabilityRepository.deleteAll(dbAvailabilities);
        availabilityRepository.saveAll(newAvailabilities);

    }

    @Transactional
    public void removeScheduleAvailability(long businessId, long scheduleId, long availabilityId) {
        scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el horario o la compañia"));

        availabilityRepository.deleteByIdAndScheduleIdAndScheduleBusinessId(availabilityId, scheduleId, businessId);
    }
}
