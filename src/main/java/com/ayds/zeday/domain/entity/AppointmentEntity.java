package com.ayds.zeday.domain.entity;

import static lombok.AccessLevel.PRIVATE;

import java.time.Instant;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ayds.zeday.domain.enums.AppointmentStateEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private LocalTime startAt;

    @NonNull
    @Column(nullable = false)
    private LocalTime endAt;

    @Builder.Default
    @Column(columnDefinition = "ENUM('SCHEDULED', 'CANCELLED', 'COMPLETED', 'NOT_ARRIVED')")
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
