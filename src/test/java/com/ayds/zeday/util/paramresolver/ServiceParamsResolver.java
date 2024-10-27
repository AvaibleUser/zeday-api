package com.ayds.zeday.util.paramresolver;

import static org.mockito.Mockito.mock;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

import com.ayds.zeday.domain.dto.service.AddServiceDto;
import com.ayds.zeday.domain.dto.service.ServiceDto;
import com.ayds.zeday.domain.dto.service.UpdateServiceDto;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.ServiceEntity;

public class ServiceParamsResolver extends RandomParamsResolver {

    public ServiceParamsResolver() {
        super(List.of(ServiceDto.class, AddServiceDto.class, UpdateServiceDto.class, ServiceEntity.class),
                List.of(ServiceDto.class));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        if (type == ServiceDto.class) {
            return getServiceDto();
        }
        if (type == AddServiceDto.class) {
            return getAddServiceDto();
        }
        if (type == UpdateServiceDto.class) {
            return getUpdateServiceDto();
        }
        if (type == ServiceEntity.class) {
            return getServiceEntity();
        }
        if (type == List.class) {
            Type genericType = parameter.getParameterizedType();
            if (genericType instanceof ParameterizedType genType) {
                Type genClass = Arrays.stream(genType.getActualTypeArguments()).findFirst().get();
                if (genClass == ServiceDto.class) {
                    return random.nextObjects(this::getServiceDto);
                }
            }
        }
        return null;
    }

    private ServiceDto getServiceDto() {
        return ServiceDto.builder()
                .id(random.nextPositiveLong())
                .name(random.nextString())
                .description(random.nextString())
                .duration(random.nextDuration())
                .cancellable(random.nextBoolean())
                .maxDaysToCancel(random.nextPositiveInt())
                .minDaysToSchedule(random.nextPositiveInt())
                .maxDaysToSchedule(random.nextPositiveInt())
                .advancePaymentPercentage(random.nextPositiveInt())
                .build();
    }

    private AddServiceDto getAddServiceDto() {
        return AddServiceDto.builder()
                .name(random.nextString())
                .description(random.nextString())
                .duration(random.nextDuration())
                .price(random.nextPositiveDouble())
                .cancellable(Optional.empty())
                .maxDaysToCancel(Optional.empty())
                .minDaysToSchedule(Optional.empty())
                .maxDaysToSchedule(Optional.empty())
                .advancePaymentPercentage(Optional.empty())
                .build();
    }

    private UpdateServiceDto getUpdateServiceDto() {
        return UpdateServiceDto.builder()
                .description(Optional.of(random.nextString()))
                .duration(Optional.of(random.nextDuration()))
                .cancellable(Optional.empty())
                .maxDaysToCancel(Optional.empty())
                .minDaysToSchedule(Optional.empty())
                .maxDaysToSchedule(Optional.empty())
                .advancePaymentPercentage(Optional.empty())
                .build();
    }

    private ServiceEntity getServiceEntity() {
        return ServiceEntity.builder()
                .name(random.nextString())
                .description(random.nextString())
                .duration(random.nextDuration())
                .price(random.nextPositiveDouble())
                .cancellable(random.nextBoolean())
                .maxDaysToCancel(random.nextPositiveInt())
                .minDaysToSchedule(random.nextPositiveInt())
                .maxDaysToSchedule(random.nextPositiveInt())
                .advancePaymentPercentage(random.nextPositiveInt())
                .business(mock(BusinessEntity.class))
                .build();
    }
}
