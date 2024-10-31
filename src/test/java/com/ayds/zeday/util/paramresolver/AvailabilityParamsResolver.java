package com.ayds.zeday.util.paramresolver;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

import com.ayds.zeday.domain.dto.availability.AddAvailabilityDto;
import com.ayds.zeday.domain.dto.availability.AvailabilityDto;
import com.ayds.zeday.domain.entity.AvailabilityEntity;
import com.ayds.zeday.domain.entity.ScheduleEntity;

public class AvailabilityParamsResolver extends RandomParamsResolver {

    public AvailabilityParamsResolver() {
        super(List.of(AvailabilityDto.class, AddAvailabilityDto.class, AvailabilityEntity.class),
                List.of(AddAvailabilityDto.class));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();

        if (type == AvailabilityDto.class) {
            return getAvailabilityDto();
        }
        if (type == AddAvailabilityDto.class) {
            return getAddAvailabilityDto();
        }
        if (type == AvailabilityEntity.class) {
            return getAvailabilityEntity();
        }
        if (type == List.class) {
            Type genericType = parameter.getParameterizedType();
            if (genericType instanceof ParameterizedType genType) {
                Type genClass = Arrays.stream(genType.getActualTypeArguments()).findFirst().get();
                if (genClass == AddAvailabilityDto.class) {
                    return random.nextObjects(this::getAddAvailabilityDto, toList());
                }
            }
        }
        return null;
    }

    private AvailabilityDto getAvailabilityDto() {
        return AvailabilityDto.builder()
                .id(random.nextPositiveLong())
                .startAt(random.nextTime())
                .endAt(random.nextTime())
                .dayOfWeek(random.nextEnum(DayOfWeek.class))
                .createdAt(random.nextInstant())
                .updatedAt(random.nextInstant())
                .build();
    }

    private AddAvailabilityDto getAddAvailabilityDto() {
        return AddAvailabilityDto.builder()
                .startAt(Optional.of(random.nextTime()))
                .endAt(Optional.of(random.nextTime()))
                .dayOfWeek(random.nextEnum(DayOfWeek.class))
                .build();
    }

    private AvailabilityEntity getAvailabilityEntity() {
        return AvailabilityEntity.builder()
                .id(random.nextPositiveLong())
                .schedule(mock(ScheduleEntity.class))
                .startAt(random.nextTime())
                .endAt(random.nextTime())
                .dayOfWeek(random.nextEnum(DayOfWeek.class))
                .createdAt(random.nextInstant())
                .updatedAt(random.nextInstant())
                .build();
    }
}
