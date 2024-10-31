package com.ayds.zeday.service.scheduling;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.util.stream.Collectors.toSet;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.zeday.domain.dto.appointment.AppointmentDto;
import com.ayds.zeday.domain.dto.appointment.GeneralAppointmentDto;
import com.ayds.zeday.domain.dto.availability.AvailabilityDto;
import com.ayds.zeday.domain.dto.schedule.AddScheduleDto;
import com.ayds.zeday.domain.dto.schedule.GeneralScheduleDto;
import com.ayds.zeday.domain.dto.schedule.ScheduleDto;
import com.ayds.zeday.domain.dto.schedule.UpdateScheduleDto;
import com.ayds.zeday.domain.dto.service.ServiceDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.domain.entity.AvailabilityEntity;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.PermissionEntity;
import com.ayds.zeday.domain.entity.RoleEntity;
import com.ayds.zeday.domain.entity.ScheduleEntity;
import com.ayds.zeday.domain.entity.ServiceEntity;
import com.ayds.zeday.domain.enums.AccessEnum;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.AppointmentRepository;
import com.ayds.zeday.repository.AvailabilityRepository;
import com.ayds.zeday.repository.BusinessRepository;
import com.ayds.zeday.repository.ScheduleRepository;
import com.ayds.zeday.repository.ServiceRepository;
import com.ayds.zeday.repository.UserRepository;

import lombok.Generated;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final BusinessRepository businessRepository;
    private final ScheduleRepository scheduleRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;

    public List<GeneralScheduleDto> findAllBusinessSchedules(long businessId) {
        return scheduleRepository.findAllByBusinessId(businessId, GeneralScheduleDto.class);
    }

    public List<ScheduleDto> findAllBusinessSchedulesByService(long businessId, LocalDate from, LocalDate to,
            Collection<Long> serviceIds) {
        return scheduleRepository.findAllByServiceIdsAndBusinessIdAndBetweenDates(serviceIds, businessId, from, to,
                ScheduleDto.class);
    }

    @Generated
    public Optional<ScheduleDto> findBusinessSchedule(long businessId, long scheduleId, LocalDate from,
            LocalDate to) {
        return scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, GeneralScheduleDto.class)
                .map(sch -> extracted(businessId, scheduleId, sch));

    }

    @Generated
    private ScheduleDto extracted(long businessId, long scheduleId, GeneralScheduleDto sch) {
        return ScheduleDto.builder()
                .id(sch.id())
                .title(sch.title())
                .notes(sch.notes())
                .availabilities(new HashSet<>(availabilityRepository.findByScheduleIdAndScheduleBusinessId(
                        scheduleId, businessId, AvailabilityDto.class)))
                .appointments(new HashSet<>(appointmentRepository.findByScheduleIdAndScheduleBusinessId(
                        scheduleId, businessId, GeneralAppointmentDto.class))
                        .stream()
                        .map(app -> extracted2(businessId, app))
                        .collect(toSet()))
                .services(new HashSet<>(serviceRepository.findByBusinessIdAndSchedulesIdIn(
                        businessId, List.of(scheduleId), ServiceDto.class)))
                .createdAt(sch.createdAt())
                .updatedAt(sch.updatedAt())
                .build();
    }

    @Generated
    private AppointmentDto extracted2(long businessId, GeneralAppointmentDto app) {
        return AppointmentDto.builder()
                .id(app.id())
                .startAt(app.startAt())
                .endAt(app.endAt())
                .state(app.state())
                .notes(app.notes())
                .service(serviceRepository.findByBusinessIdAndAppointmentsIdIn(businessId,
                        List.of(app.id()), ServiceDto.class).get())
                .createdAt(app.createdAt())
                .updatedAt(app.updatedAt())
                .build();
    }

    @Generated
    public List<UserDto> findPossibleAttendantInBusinessSchedule(long businessId, long scheduleId, long serviceId,
            Instant from, Instant to) {
        BusinessEntity business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la empresa"));

        List<UserDto> attendants = userRepository.findAllByPossibleAttendantBetweenDates(businessId, scheduleId,
                serviceId, from, to, UserDto.class);

        return business.getAutoAssignment() ? attendants.subList(0, 1) : attendants;
    }

    @Transactional
    public void addBusinessSchedule(long businessId, AddScheduleDto schedule) {
        BusinessEntity business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la compañia actual"));

        if (scheduleRepository.existsByTitleAndBusinessId(schedule.title(), businessId)) {
            throw new RequestConflictException("El titulo del horario que se intenta registrar ya esta en uso");
        }

        PermissionEntity readPermission = PermissionEntity.builder()
                .module("SCHEDULE")
                .grantAccess(AccessEnum.READ)
                .build();

        RoleEntity attendantRole = RoleEntity.builder()
                .name("attendant@" + businessId + "@" + schedule.title())
                .description("Role creato para identificar a los encargados del horario '" + schedule.title() + "'")
                .multiuser(true)
                .business(business)
                .permissions(new HashSet<>(Set.of(readPermission)))
                .build();

        ScheduleEntity newSchedule = ScheduleEntity.builder()
                .title(schedule.title())
                .permission(readPermission)
                .role(attendantRole)
                .business(business)
                .build();

        newSchedule = scheduleRepository.saveAndFlush(newSchedule);

        AvailabilityEntity monday = AvailabilityEntity.builder()
                .startAt(LocalTime.of(8, 0))
                .endAt(LocalTime.of(17, 0))
                .recurring(true)
                .dayOfWeek(MONDAY)
                .schedule(newSchedule)
                .build();

        AvailabilityEntity tuesday = monday.withDayOfWeek(TUESDAY);
        AvailabilityEntity wednesday = monday.withDayOfWeek(WEDNESDAY);
        AvailabilityEntity thursday = monday.withDayOfWeek(THURSDAY);
        AvailabilityEntity friday = monday.withDayOfWeek(FRIDAY);

        availabilityRepository.saveAll(List.of(monday, tuesday, wednesday, thursday, friday));
    }

    @Transactional
    public void updateBusinessSchedule(long businessId, long scheduleId, UpdateScheduleDto schedule) {
        ScheduleEntity scheduleDb = scheduleRepository
                .findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("El horario no se pudo encontrar"));

        scheduleDb.setNotes(schedule.notes());

        scheduleRepository.save(scheduleDb);
    }

    @Transactional
    public void toggleServicesToBusinessSchedule(long businessId, long scheduleId, List<Long> serviceIds) {
        ScheduleEntity schedule = scheduleRepository
                .findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("El horario no se pudo encontrar"));

        schedule.getServices().removeIf(service -> !serviceIds.contains(service.getId()));

        List<Long> actualServiceIds = schedule.getServices()
                .stream()
                .map(ServiceEntity::getId)
                .toList();

        serviceIds.removeAll(actualServiceIds);

        List<ServiceEntity> servicesToAdd = serviceRepository.findAllById(serviceIds);

        if (!serviceIds.containsAll(servicesToAdd.stream().map(ServiceEntity::getId).toList())) {
            throw new ValueNotFoundException("No se pudieron encontrar todos los servicios");
        }

        schedule.getServices().addAll(servicesToAdd);

        scheduleRepository.save(schedule);
    }
}
