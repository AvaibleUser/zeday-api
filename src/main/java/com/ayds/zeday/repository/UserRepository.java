package com.ayds.zeday.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ayds.zeday.domain.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    <U> Optional<U> findUserById(Long id, Class<U> type);

    <U> Optional<U> findByIdAndBusinessId(Long id, Long businessId, Class<U> type);

    <U> Optional<U> findByEmailAndBusinessId(String email, Long businessId, Class<U> type);

    boolean existsByEmailAndBusinessId(String email, Long businessId);

    @Query("""
            SELECT user, COUNT(aa) AS assigned FROM user u
                INNER JOIN u.assignedAppointments aa
                INNER JOIN u.roles r
                INNER JOIN r.permissions p
                INNER JOIN p.schedule s
                INNER JOIN s.services se
                WHERE s.id = :scheduleId AND se.id = :serviceId AND u.business.id = :businessId AND
                    CAST(aa.state AS java.lang.String) = 'SCHEDULED' AND
                    (:from NOT BETWEEN aa.startAt AND aa.endAt OR :to NOT BETWEEN aa.startAt AND aa.endAt)
                GROUP By u
                ORDER BY assigned ASC
            """)
    <U> List<U> findAllByPossibleAttendantBetweenDates(@Param("businessId") Long businessId,
            @Param("scheduleId") Long scheduleId, @Param("scheduleId") Long serviceId, @Param("from") Instant from,
            @Param("to") Instant to, Class<U> type);

    @Query("""
            SELECT COUNT(user) > 0 FROM user u
                INNER JOIN u.scheduledAppointments sa
                WHERE u.id = :id AND CAST(sa.state AS java.lang.String) = 'SCHEDULED' AND
                    (:from BETWEEN sa.startAt AND sa.endAt OR :to BETWEEN sa.startAt AND sa.endAt)
            """)
    boolean existsByIdAndBusinessIdAndWithScheduledAppointmentBetweenDates(@Param("id") Long id,
            @Param("from") Instant from, @Param("to") Instant to);

    @Query("""
            SELECT COUNT(user) > 0 FROM user u
                INNER JOIN u.assignedAppointments aa
                WHERE u.id = :id AND CAST(aa.state AS java.lang.String) = 'SCHEDULED' AND
                    (:from BETWEEN aa.startAt AND aa.endAt OR :to BETWEEN aa.startAt AND aa.endAt)
            """)
    boolean existsByIdAndBusinessIdAndWithAssignedAppointmentBetweenDates(@Param("id") Long id,
            @Param("from") Instant from, @Param("to") Instant to);
}
