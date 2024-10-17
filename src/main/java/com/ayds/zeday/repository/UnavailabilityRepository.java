package com.ayds.zeday.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.zeday.domain.entity.UnavailabilityEntity;

@Repository
public interface UnavailabilityRepository extends JpaRepository<UnavailabilityEntity, Long> {

    Optional<UnavailabilityEntity> findByIdAndScheduleIdAndScheduleBusinessId(Long id, Long scheduleId,
            Long businessId);

    boolean existsByScheduleIdAndSpecificDay(Long scheduleId, LocalDate specificDay);

    void deleteByIdAndScheduleIdAndScheduleBusinessId(Long id, Long scheduleId, Long businessId);
}
