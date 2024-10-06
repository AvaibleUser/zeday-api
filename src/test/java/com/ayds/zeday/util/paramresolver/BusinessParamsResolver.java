package com.ayds.zeday.util.paramresolver;

import java.lang.reflect.Parameter;
import java.util.List;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

import com.ayds.zeday.domain.dto.business.AddBusinessDto;
import com.ayds.zeday.domain.dto.business.BusinessDto;
import com.ayds.zeday.domain.dto.business.UpdateBusinessDto;
import com.ayds.zeday.domain.entity.BusinessEntity;

public class BusinessParamsResolver extends RandomParamsResolver {

    public BusinessParamsResolver() {
        super(List.of(BusinessDto.class, AddBusinessDto.class, UpdateBusinessDto.class, BusinessEntity.class));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        if (type == BusinessDto.class) {
            return getBusinessDto();
        }
        if (type == AddBusinessDto.class) {
            return getAddBusinessDto();
        }
        if (type == UpdateBusinessDto.class) {
            return getUpdateBusinessDto();
        }
        if (type == BusinessEntity.class) {
            return getBusinessEntity();
        }
        return null;
    }

    private BusinessDto getBusinessDto() {
        return BusinessDto.builder()
                .id(random.nextPositiveLong())
                .name(random.nextString())
                .autoAssignment(random.nextBoolean())
                .createdAt(random.nextInstant())
                .updatedAt(random.nextInstant())
                .build();
    }

    private AddBusinessDto getAddBusinessDto() {
        return AddBusinessDto.builder()
                .name(random.nextString())
                .autoAssignment(random.nextBoolean())
                .build();
    }

    private UpdateBusinessDto getUpdateBusinessDto() {
        return UpdateBusinessDto.builder()
                .autoAssignment(random.nextBoolean())
                .build();
    }

    private BusinessEntity getBusinessEntity() {
        return BusinessEntity.builder()
                .id(random.nextPositiveLong())
                .name(random.nextString())
                .autoAssignment(random.nextBoolean())
                .logoUrl(random.nextString())
                .build();
    }
}
