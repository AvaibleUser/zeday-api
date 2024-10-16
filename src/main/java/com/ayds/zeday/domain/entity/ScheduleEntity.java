package com.ayds.zeday.domain.entity;

import static jakarta.persistence.CascadeType.PERSIST;
import static lombok.AccessLevel.PRIVATE;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "schedule")
@Table(name = "schedule", uniqueConstraints = @UniqueConstraint(columnNames = { "title", "business_id" }))
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String title;

    @Column
    private String notes;

    @NonNull
    @OneToOne(optional = false, cascade = PERSIST)
    @JoinColumn(name = "permission_id")
    private PermissionEntity permission;

    @NonNull
    @OneToOne(optional = false, cascade = PERSIST)
    @JoinColumn(name = "role_id")
    private RoleEntity role;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "business_id")
    private BusinessEntity business;

    @OneToMany(mappedBy = "schedule")
    private Set<AvailabilityEntity> availabilities;

    @OneToMany(mappedBy = "schedule")
    private Set<UnavailabilityEntity> unavailabilities;

    @OneToMany(mappedBy = "schedule")
    private Set<AppointmentEntity> appointments;

    @ManyToMany
    @JoinTable(name = "schedule_service", joinColumns = @JoinColumn(name = "schedule_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private Set<ServiceEntity> services;

    @CreationTimestamp
    @Column
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;
}
