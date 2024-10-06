package com.ayds.zeday.domain.entity;

import static lombok.AccessLevel.PRIVATE;

import java.time.Duration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "service", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "business_id" }))
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@EqualsAndHashCode
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @Setter
    @NonNull
    @Column(nullable = false)
    private String description;

    @Setter
    @NonNull
    @Column(nullable = false)
    private Duration duration;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "business_id")
    private BusinessEntity business;
}
