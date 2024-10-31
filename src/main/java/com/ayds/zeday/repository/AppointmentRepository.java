package com.ayds.zeday.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.zeday.domain.entity.AppointmentEntity;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    <U> Optional<U> findByIdAndScheduleId(Long id, Long scheduleId, Class<U> type);

    <A> List<A> findByScheduleIdAndScheduleBusinessId(Long scheduleId, Long businessId, Class<A> type);
}
