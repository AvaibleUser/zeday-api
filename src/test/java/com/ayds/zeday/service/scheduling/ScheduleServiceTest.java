package com.ayds.zeday.service.scheduling;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.ayds.zeday.domain.dto.schedule.AddScheduleDto;
import com.ayds.zeday.domain.dto.schedule.GeneralScheduleDto;
import com.ayds.zeday.domain.dto.schedule.ScheduleDto;
import com.ayds.zeday.domain.dto.schedule.UpdateScheduleDto;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.ScheduleEntity;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.AvailabilityRepository;
import com.ayds.zeday.repository.BusinessRepository;
import com.ayds.zeday.repository.ScheduleRepository;
import com.ayds.zeday.util.annotation.ZedayTest;
import com.ayds.zeday.util.paramresolver.BusinessParamsResolver;
import com.ayds.zeday.util.paramresolver.ScheduleParamsResolver;

@ZedayTest
@ExtendWith({ ScheduleParamsResolver.class, BusinessParamsResolver.class })
public class ScheduleServiceTest {

    @Captor
    private ArgumentCaptor<ScheduleEntity> scheduleCaptor;

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @Test
    public void canFindAllBusinessSchedules(long businessId, List<GeneralScheduleDto> expectedSchedules) {
        given(scheduleRepository.findAllByBusinessId(businessId, GeneralScheduleDto.class))
                .willReturn(expectedSchedules.stream().toList());

        List<GeneralScheduleDto> actualSchedules = scheduleService.findAllBusinessSchedules(businessId);

        then(actualSchedules).isEqualTo(expectedSchedules);
    }

    @Test
    public void canFindAllBusinessSchedulesByService(long businessId, LocalDate from, LocalDate to,
            List<Long> serviceIds, List<ScheduleDto> expectedSchedules) {
        given(scheduleRepository.findAllByServiceIdsAndBusinessIdAndBetweenDates(serviceIds, businessId, from, to,
                ScheduleDto.class))
                .willReturn(expectedSchedules.stream().toList());

        List<ScheduleDto> actualSchedules = scheduleService.findAllBusinessSchedulesByService(businessId, from, to,
                serviceIds);

        then(actualSchedules).isEqualTo(expectedSchedules);
    }

    @Test
    public void canAddBusinessSchedule(long businessId, BusinessEntity business, ScheduleEntity expectedSchedule) {
        AddScheduleDto schedule = AddScheduleDto.builder()
                .title(expectedSchedule.getTitle())
                .build();

        given(businessRepository.findById(businessId)).willReturn(Optional.of(business.toBuilder().build()));
        given(scheduleRepository.existsByTitleAndBusinessId(schedule.title(), businessId)).willReturn(false);
        given(scheduleRepository.saveAndFlush(isA(ScheduleEntity.class))).willReturn(expectedSchedule);
        given(availabilityRepository.saveAll(anyIterable())).willReturn(null);

        scheduleService.addBusinessSchedule(businessId, schedule);

        verify(scheduleRepository).saveAndFlush(scheduleCaptor.capture());
        then(scheduleCaptor.getValue()).extracting(ScheduleEntity::getTitle).isEqualTo(expectedSchedule.getTitle());
        then(scheduleCaptor.getValue()).extracting(ScheduleEntity::getPermission).isNotNull();
        then(scheduleCaptor.getValue()).extracting(ScheduleEntity::getRole).isNotNull();
        then(scheduleCaptor.getValue()).extracting(ScheduleEntity::getBusiness).isEqualTo(business);
    }

    @Test
    public void canBlockAddBusinessScheduleWithBusinessNotFound(long businessId, AddScheduleDto schedule) {
        given(businessRepository.findById(businessId)).willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> scheduleService.addBusinessSchedule(businessId, schedule));
    }

    @Test
    public void canBlockAddBusinessScheduleWithDuplicateTitle(long businessId, AddScheduleDto schedule,
            BusinessEntity business) {
        given(businessRepository.findById(businessId)).willReturn(Optional.of(business.toBuilder().build()));
        given(scheduleRepository.existsByTitleAndBusinessId(schedule.title(), businessId)).willReturn(true);

        assertThrows(RequestConflictException.class, () -> scheduleService.addBusinessSchedule(businessId, schedule));
    }

    @Test
    public void canUpdateBusinessSchedule(long businessId, long scheduleId, UpdateScheduleDto expectedSchedule,
            ScheduleEntity schedule) {
        given(scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class))
                .willReturn(Optional.of(schedule));

        scheduleService.updateBusinessSchedule(businessId, scheduleId, expectedSchedule.toBuilder().build());

        verify(scheduleRepository).save(scheduleCaptor.capture());
        then(scheduleCaptor.getValue()).extracting(ScheduleEntity::getNotes).isEqualTo(expectedSchedule.notes());
    }

    @Test
    public void canBlockUpdateBusinessScheduleWithScheduleNotFound(long businessId, long scheduleId,
            UpdateScheduleDto schedule) {
        given(scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class))
                .willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> scheduleService.updateBusinessSchedule(businessId, scheduleId,
                schedule));
    }
}
