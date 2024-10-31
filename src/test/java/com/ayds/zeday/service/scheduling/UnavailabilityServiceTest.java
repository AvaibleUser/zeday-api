package com.ayds.zeday.service.scheduling;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.ayds.zeday.domain.dto.unavailability.AddUnavailabilityDto;
import com.ayds.zeday.domain.dto.unavailability.UpdateUnavailabilityDto;
import com.ayds.zeday.domain.entity.ScheduleEntity;
import com.ayds.zeday.domain.entity.UnavailabilityEntity;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.ScheduleRepository;
import com.ayds.zeday.repository.UnavailabilityRepository;
import com.ayds.zeday.util.annotation.ZedayTest;
import com.ayds.zeday.util.paramresolver.ScheduleParamsResolver;
import com.ayds.zeday.util.paramresolver.UnavailabilityParamsResolver;

@ZedayTest
@ExtendWith({ UnavailabilityParamsResolver.class, ScheduleParamsResolver.class })
public class UnavailabilityServiceTest {

    @Captor
    private ArgumentCaptor<UnavailabilityEntity> unavailabilityCaptor;

    @Mock
    private UnavailabilityRepository unavailabilityRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private UnavailabilityService unavailabilityService;

    @Test
    public void canAddScheduleUnavailability(long businessId, long scheduleId,
            UnavailabilityEntity expectedUnavailability, ScheduleEntity schedule) {
        AddUnavailabilityDto unavailability = AddUnavailabilityDto.builder()
                .startAt(expectedUnavailability.getStartAt())
                .endAt(expectedUnavailability.getEndAt())
                .specificDay(expectedUnavailability.getSpecificDay())
                .build();

        given(scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class))
                .willReturn(Optional.of(schedule));

        unavailabilityService.addScheduleUnavailability(businessId, scheduleId, unavailability);

        verify(unavailabilityRepository).save(unavailabilityCaptor.capture());
        then(unavailabilityCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("schedule")
                .isEqualTo(expectedUnavailability);
    }

    @Test
    public void canBlockAddScheduleUnavailabilityWithScheduleNotFound(long businessId, long scheduleId,
            AddUnavailabilityDto unavailability) {
        given(scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class))
                .willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> unavailabilityService.addScheduleUnavailability(businessId,
                scheduleId, unavailability));
    }

    @Test
    public void canBlockAddScheduleUnavailabilityWithSpecificDayAlreadyInUse(long businessId, long scheduleId,
            AddUnavailabilityDto unavailability, ScheduleEntity schedule) {
        given(scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class))
                .willReturn(Optional.of(schedule));
        given(unavailabilityRepository.existsByScheduleIdAndSpecificDay(scheduleId, unavailability.specificDay()))
                .willReturn(true);

        assertThrows(RequestConflictException.class, () -> unavailabilityService.addScheduleUnavailability(businessId,
                scheduleId, unavailability));
    }

    @Test
    public void canUpdateScheduleUnavailability(long businessId, long scheduleId, long unavailabilityId,
            UpdateUnavailabilityDto expectedUnavailability, UnavailabilityEntity unavailability) {
        given(unavailabilityRepository.findByIdAndScheduleIdAndScheduleBusinessId(unavailabilityId, scheduleId,
                businessId))
                .willReturn(Optional.of(unavailability));

        unavailabilityService.updateScheduleUnavailability(businessId, scheduleId, unavailabilityId,
                expectedUnavailability);

        verify(unavailabilityRepository).save(unavailabilityCaptor.capture());
        then(unavailabilityCaptor.getValue()).extracting(UnavailabilityEntity::getStartAt).isEqualTo(
                expectedUnavailability.startAt().get());
        then(unavailabilityCaptor.getValue()).extracting(UnavailabilityEntity::getEndAt).isEqualTo(
                expectedUnavailability.endAt().get());
        then(unavailabilityCaptor.getValue()).extracting(UnavailabilityEntity::getSpecificDay).isEqualTo(
                expectedUnavailability.specificDay().get());
    }

    @Test
    public void canBlockUpdateScheduleUnavailabilityWithUnavailabilityNotFound(long businessId, long scheduleId,
            long unavailabilityId, UpdateUnavailabilityDto unavailability) {
        given(unavailabilityRepository.findByIdAndScheduleIdAndScheduleBusinessId(unavailabilityId, scheduleId,
                businessId))
                .willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> unavailabilityService.updateScheduleUnavailability(businessId,
                scheduleId, unavailabilityId, unavailability));
    }

    @Test
    public void canRemoveScheduleUnavailability(long businessId, long scheduleId, long unavailabilityId,
            ScheduleEntity schedule) {
        given(scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class))
                .willReturn(Optional.of(schedule));

        unavailabilityService.removeScheduleUnavailability(businessId, scheduleId, unavailabilityId);

        verify(unavailabilityRepository).deleteByIdAndScheduleIdAndScheduleBusinessId(unavailabilityId, scheduleId,
                businessId);
    }

    @Test
    public void canBlockRemoveScheduleUnavailabilityWithUnavailabilityNotFound(long businessId, long scheduleId,
            long unavailabilityId) {
        given(scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class))
                .willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> unavailabilityService.removeScheduleUnavailability(businessId,
                scheduleId, unavailabilityId));
    }
}
