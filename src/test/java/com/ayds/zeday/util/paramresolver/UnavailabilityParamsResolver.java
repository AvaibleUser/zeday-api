package com.ayds.zeday.util.paramresolver;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

import com.ayds.zeday.domain.dto.unavailability.AddUnavailabilityDto;
import com.ayds.zeday.domain.dto.unavailability.UpdateUnavailabilityDto;
import com.ayds.zeday.domain.entity.ScheduleEntity;
import com.ayds.zeday.domain.entity.UnavailabilityEntity;

public class UnavailabilityParamsResolver extends RandomParamsResolver {

    public UnavailabilityParamsResolver() {
        super(List.of(AddUnavailabilityDto.class, UpdateUnavailabilityDto.class, UnavailabilityEntity.class));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        if (type == AddUnavailabilityDto.class) {
            return getAddUnavailabilityDto();
        }
        if (type == UpdateUnavailabilityDto.class) {
            return getUpdateUnavailabilityDto();
        }
        if (type == UnavailabilityEntity.class) {
            return getUnavailabilityEntity();
        }
        return null;
    }

    private AddUnavailabilityDto getAddUnavailabilityDto() {
        return AddUnavailabilityDto.builder()
                .startAt(random.nextTime())
                .endAt(random.nextTime())
                .specificDay(random.nextDate())
                .build();
    }

    private UpdateUnavailabilityDto getUpdateUnavailabilityDto() {
        return UpdateUnavailabilityDto.builder()
                .startAt(Optional.of(random.nextTime()))
                .endAt(Optional.of(random.nextTime()))
                .specificDay(Optional.of(random.nextDate()))
                .build();
    }

    private UnavailabilityEntity getUnavailabilityEntity() {
        return UnavailabilityEntity.builder()
                .startAt(random.nextTime())
                .endAt(random.nextTime())
                .specificDay(random.nextDate())
                .schedule(mock(ScheduleEntity.class))
                .build();
    }
}
