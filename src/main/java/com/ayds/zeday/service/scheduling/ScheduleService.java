package com.ayds.zeday.service.scheduling;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.zeday.domain.dto.schedule.AddScheduleDto;
import com.ayds.zeday.domain.dto.schedule.GeneralScheduleDto;
import com.ayds.zeday.domain.dto.schedule.ScheduleDto;
import com.ayds.zeday.domain.dto.schedule.UpdateScheduleDto;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.PermissionEntity;
import com.ayds.zeday.domain.entity.RoleEntity;
import com.ayds.zeday.domain.entity.ScheduleEntity;
import com.ayds.zeday.domain.enums.AccessEnum;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.BusinessRepository;
import com.ayds.zeday.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final BusinessRepository businessRepository;
    private final ScheduleRepository scheduleRepository;

    public List<GeneralScheduleDto> findAllBusinessSchedules(long businessId) {
        return scheduleRepository.findAllByBusinessId(businessId, GeneralScheduleDto.class);
    }

    public List<ScheduleDto> findAllBusinessSchedulesByService(long businessId, LocalDate from, LocalDate to,
            Collection<Long> serviceIds) {
        return scheduleRepository.findAllByServiceIdsAndBusinessIdAndBetweenDates(serviceIds, businessId, from, to,
                ScheduleDto.class);
    }

    public Optional<ScheduleDto> findBusinessSchedule(long businessId, long scheduleId, LocalDate from,
            LocalDate to) {
        return scheduleRepository.findByIdAndBusinessIdAndBetweenDates(scheduleId, businessId, from, to,
                ScheduleDto.class);
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
                .permission(readPermission)
                .build();

        ScheduleEntity newSchedule = ScheduleEntity.builder()
                .title(schedule.title())
                .permission(readPermission)
                .role(attendantRole)
                .business(business)
                .build();

        scheduleRepository.save(newSchedule);
    }

    @Transactional
    public void updateBusinessSchedule(long businessId, long scheduleId, UpdateScheduleDto schedule) {
        ScheduleEntity scheduleDb = scheduleRepository
                .findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("El horario no se pudo encontrar"));

        scheduleDb.setNotes(schedule.notes());

        scheduleRepository.save(scheduleDb);
    }
}
