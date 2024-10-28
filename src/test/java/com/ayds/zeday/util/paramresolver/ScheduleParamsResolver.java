package com.ayds.zeday.util.paramresolver;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

import com.ayds.zeday.domain.dto.appointment.AppointmentDto;
import com.ayds.zeday.domain.dto.availability.AvailabilityDto;
import com.ayds.zeday.domain.dto.schedule.AddScheduleDto;
import com.ayds.zeday.domain.dto.schedule.GeneralScheduleDto;
import com.ayds.zeday.domain.dto.schedule.ScheduleDto;
import com.ayds.zeday.domain.dto.schedule.UpdateScheduleDto;
import com.ayds.zeday.domain.dto.service.ServiceDto;
import com.ayds.zeday.domain.entity.ScheduleEntity;
import com.ayds.zeday.domain.entity.ServiceEntity;

public class ScheduleParamsResolver extends RandomParamsResolver {

    public ScheduleParamsResolver() {
        super(List.of(GeneralScheduleDto.class, ScheduleDto.class, AddScheduleDto.class, UpdateScheduleDto.class,
                ScheduleEntity.class),
                List.of(GeneralScheduleDto.class));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        if (type == GeneralScheduleDto.class) {
            return getGeneralScheduleDto();
        }
        if (type == ScheduleDto.class) {
            return getScheduleDto();
        }
        if (type == AddScheduleDto.class) {
            return getAddScheduleDto();
        }
        if (type == UpdateScheduleDto.class) {
            return getUpdateScheduleDto();
        }
        if (type == ScheduleEntity.class) {
            return getScheduleEntity();
        }
        if (type == List.class) {
            Type genericType = parameter.getParameterizedType();
            if (genericType instanceof ParameterizedType genType) {
                Type genClass = Arrays.stream(genType.getActualTypeArguments()).findFirst().get();
                if (genClass == GeneralScheduleDto.class) {
                    return random.nextObjects(this::getGeneralScheduleDto, toList());
                }
            }
        }
        return null;
    }

    private GeneralScheduleDto getGeneralScheduleDto() {
        return GeneralScheduleDto.builder()
                .id(random.nextPositiveLong())
                .title(random.nextString())
                .notes(random.nextString())
                .createdAt(random.nextInstant())
                .updatedAt(random.nextInstant())
                .build();
    }

    private ScheduleDto getScheduleDto() {
        return ScheduleDto.builder()
                .id(random.nextPositiveLong())
                .title(random.nextString())
                .notes(random.nextString())
                .availabilities(random.nextMocks(AvailabilityDto.class, toSet()))
                .appointments(random.nextMocks(AppointmentDto.class, toSet()))
                .services(random.nextMocks(ServiceDto.class, toSet()))
                .createdAt(random.nextInstant())
                .updatedAt(random.nextInstant())
                .build();
    }

    private AddScheduleDto getAddScheduleDto() {
        return AddScheduleDto.builder()
                .title(random.nextString())
                .build();
    }

    private UpdateScheduleDto getUpdateScheduleDto() {
        return UpdateScheduleDto.builder()
                .notes(random.nextString())
                .build();
    }

    private ScheduleEntity getScheduleEntity() {
        return ScheduleEntity.builder()
                .id(random.nextPositiveLong())
                .title(random.nextString())
                .notes(random.nextString())
                .services(random.nextMocks(ServiceEntity.class, toSet()))
                .createdAt(random.nextInstant())
                .updatedAt(random.nextInstant())
                .build();
    }
}
