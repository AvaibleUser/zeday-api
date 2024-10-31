package com.ayds.zeday.domain.entity;

import static lombok.AccessLevel.PRIVATE;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "service")
@Table(name = "service", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "business_id" }))
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false)
    private String description;

    @NonNull
    @Column(nullable = false)
    private Duration duration;

    @NonNull
    @Column(nullable = false)
    private Double price;

    @NonNull
    @Column(nullable = false)
    private Boolean cancellable;

    @NonNull
    @Column(nullable = false)
    private Integer maxDaysToCancel;

    @NonNull
    @Column(nullable = false)
    private Integer minDaysToSchedule;

    @NonNull
    @Column(nullable = false)
    private Integer maxDaysToSchedule;

    @NonNull
    @Column(nullable = false)
    private Integer advancePaymentPercentage;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "business_id")
    private BusinessEntity business;

    @ManyToMany(mappedBy = "services")
    private Set<ScheduleEntity> schedules;

    @OneToMany(mappedBy = "service")
    private Set<AppointmentEntity> appointments;

    @CreationTimestamp
    @Column
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;
}
