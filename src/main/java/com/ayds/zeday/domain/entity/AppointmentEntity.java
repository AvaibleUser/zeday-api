package com.ayds.zeday.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

import java.time.Instant;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ayds.zeday.domain.enums.AppointmentStateEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "appointment")
@Table(name = "appointment")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private Instant startAt;

    @NonNull
    @Column(nullable = false)
    private Instant endAt;

    @Builder.Default
    @Enumerated(STRING)
    private AppointmentStateEnum state = AppointmentStateEnum.SCHEDULED;

    private String notes;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    private UserEntity customer;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity schedule;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id")
    private ServiceEntity service;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "attendant_id")
    private UserEntity attendant;

    @CreationTimestamp
    @Column
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;
}
