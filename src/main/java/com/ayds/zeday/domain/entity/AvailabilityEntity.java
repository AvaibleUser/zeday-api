package com.ayds.zeday.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "availability")
@Table(name = "availability")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class AvailabilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private LocalTime startAt;

    @NonNull
    @Column(nullable = false)
    private LocalTime endAt;

    @NonNull
    @Column(nullable = false)
    private Boolean recurring;

    @Enumerated(STRING)
    private DayOfWeek dayOfWeek;

    private LocalDate specificDay;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity schedule;

    @CreationTimestamp
    @Column
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;
}
