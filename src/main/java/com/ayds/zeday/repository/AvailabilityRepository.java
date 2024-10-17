package com.ayds.zeday.repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.zeday.domain.entity.AvailabilityEntity;

@Repository
public interface AvailabilityRepository extends JpaRepository<AvailabilityEntity, Long> {

    Optional<AvailabilityEntity> findByIdAndScheduleIdAndScheduleBusinessId(Long id, Long scheduleId, Long businessId);

    boolean existsByScheduleIdAndRecurringAndDayOfWeek(Long scheduleId, Boolean recurring, DayOfWeek dayOfWeek);

    boolean existsByScheduleIdAndRecurringAndSpecificDay(Long scheduleId, Boolean recurring, LocalDate specificDay);

    void deleteByIdAndScheduleIdAndScheduleBusinessId(Long id, Long scheduleId, Long businessId);
}
