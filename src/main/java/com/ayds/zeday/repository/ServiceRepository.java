package com.ayds.zeday.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.zeday.domain.entity.ServiceEntity;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    <S> List<S> findAllByBusinessId(Long businessId, Class<S> type);

    <S> List<S> findByBusinessIdAndSchedulesIdIn(Long businessId, List<Long> scheduleIds, Class<S> type); 

    <S> Optional<S> findByBusinessIdAndAppointmentsIdIn(Long businessId, List<Long> appointmentIds, Class<S> type); 

    <S> Optional<S> findByIdAndBusinessId(Long id, Long businessId, Class<S> type);

    boolean existsByNameAndBusinessId(String name, Long businessId);

    void deleteByIdAndBusinessId(Long id, Long businessId);
}
