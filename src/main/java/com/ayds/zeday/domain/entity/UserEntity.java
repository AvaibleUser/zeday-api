package com.ayds.zeday.domain.entity;

import static jakarta.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity(name = "user")
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "email", "business_id" }),
        @UniqueConstraint(columnNames = { "nit", "business_id" }),
        @UniqueConstraint(columnNames = { "cui", "business_id" }) })
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String email;

    @NonNull
    @Column(nullable = false)
    private String password;

    @NonNull
    @Column(nullable = false)
    private String mfaSecret;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false)
    private String lastname;

    @NonNull
    @Column(nullable = false)
    private String nit;

    @NonNull
    @Column(nullable = false)
    private String cui;

    @NonNull
    @Column(nullable = false)
    private String phone;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activeMfa = false;

    private String timezone;

    @NonNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "business_id")
    private BusinessEntity business;

    @NonNull
    @ManyToMany(fetch = EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;

    @OneToMany(mappedBy = "customer")
    private Set<AppointmentEntity> scheduledAppointments;

    @OneToMany(mappedBy = "attendant")
    private Set<AppointmentEntity> assignedAppointments;

    @CreationTimestamp
    @Column
    private Instant createdAt;

    @UpdateTimestamp
    @Column
    private Instant updatedAt;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(RoleEntity::getPermissions)
                .flatMap(Set::stream)
                .distinct()
                .map(permission -> permission.getModule().toUpperCase()
                        + (isEmpty(permission.getSchedule()) ? "" : "@" + permission.getSchedule())
                        + "::" + permission.getGrantAccess().name())
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
