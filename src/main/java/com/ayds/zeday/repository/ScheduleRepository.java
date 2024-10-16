package com.ayds.zeday.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ayds.zeday.domain.entity.ScheduleEntity;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

    <S> List<S> findAllByBusinessId(Long businessId, Class<S> type);

    <S> Optional<S> findByIdAndBusinessId(Long id, Long businessId, Class<S> type);

    boolean existsByTitleAndBusinessId(String title, Long businessId);

    @Query("""
            SELECT s FROM schedule s
                INNER JOIN s.availabilities a
                INNER JOIN s.unavailabilities u
                INNER JOIN s.appointments ap
                INNER JOIN s.services se
                WHERE se.id IN (:serviceIds) AND s.business.id = :businessId AND (
                    (a.recurring AND CAST(a.dayOfWeek AS java.lang.String) != DAYNAME(u.specificDay)) OR
                    (a.specificDay BETWEEN :from AND :to AND a.specificDay != u.specificDay))
            """)
    <S> List<S> findAllByServiceIdsAndBusinessIdAndBetweenDates(@Param("serviceIds") Collection<Long> serviceIds,
            @Param("businessId") Long businessId, @Param("from") LocalDate from, @Param("to") LocalDate to,
            Class<S> type);

    @Query("""
            SELECT s FROM schedule s
                INNER JOIN s.availabilities a
                INNER JOIN s.unavailabilities u
                INNER JOIN s.appointments ap
                WHERE s.id = :id AND s.business.id = :businessId AND (
                    (a.recurring AND CAST(a.dayOfWeek AS java.lang.String) != DAYNAME(u.specificDay)) OR
                    (a.specificDay BETWEEN :from AND :to AND a.specificDay != u.specificDay))
            """)
    <S> Optional<S> findByIdAndBusinessIdAndBetweenDates(@Param("id") Long id, @Param("businessId") Long businessId,
            @Param("from") LocalDate from, @Param("to") LocalDate to, Class<S> type);
}
