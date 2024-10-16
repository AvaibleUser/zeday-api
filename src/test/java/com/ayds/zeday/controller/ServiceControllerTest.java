package com.ayds.zeday.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import com.ayds.zeday.domain.dto.service.AddServiceDto;
import com.ayds.zeday.domain.dto.service.ServiceDto;
import com.ayds.zeday.domain.dto.service.UpdateServiceDto;
import com.ayds.zeday.service.business.ServiceService;
import com.ayds.zeday.util.annotation.ZedayTest;
import com.ayds.zeday.util.paramresolver.ServiceParamsResolver;

@ZedayTest
@ExtendWith(ServiceParamsResolver.class)
public class ServiceControllerTest {

    @MockBean
    private ServiceService serviceService;

    @Autowired
    private ServiceController serviceController;

    @Test
    public void canGetBusinessServices(long businessId, List<ServiceDto> expectedServices) {
        given(serviceService.findBusinessServices(businessId)).willReturn(expectedServices.stream().toList());

        ResponseEntity<List<ServiceDto>> actualServices = serviceController.getBusinessServices(businessId);

        then(actualServices)
                .extracting(ResponseEntity::getBody)
                .isEqualTo(expectedServices);

        then(actualServices)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(OK);
    }

    @Test
    public void canGetBusinessService(long businessId, ServiceDto expectedService) {
        long serviceId = expectedService.id();

        given(serviceService.findService(businessId, serviceId))
                .willReturn(Optional.of(expectedService.toBuilder().build()));

        ResponseEntity<ServiceDto> actualService = serviceController.getBusinessService(businessId, serviceId);

        then(actualService)
                .extracting(ResponseEntity::getBody)
                .isEqualTo(expectedService);

        then(actualService)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(OK);
    }

    @Test
    public void canNotFoundCurrentBusiness(long businessId, long serviceId) {
        given(serviceService.findService(businessId, serviceId)).willReturn(Optional.empty());

        ResponseEntity<ServiceDto> actualService = serviceController.getBusinessService(businessId, serviceId);

        then(actualService)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(NOT_FOUND);
    }

    @Test
    public void canCreateBusinessService(long expectedBusinessId, AddServiceDto expectedService) {
        willDoNothing().given(serviceService).addService(isA(Long.class), isA(AddServiceDto.class));

        serviceController.createBusinessService(expectedBusinessId, expectedService.toBuilder().build());

        verify(serviceService).addService(expectedBusinessId, expectedService);
    }

    @Test
    public void canUpdateBusinessService(long expectedBusinessId, UpdateServiceDto expectedService) {
        willDoNothing().given(serviceService)
                .updateService(isA(Long.class), isA(Long.class), isA(UpdateServiceDto.class));

        serviceController.updateBusinessService(expectedBusinessId, expectedBusinessId,
                expectedService.toBuilder().build());

        verify(serviceService).updateService(expectedBusinessId, expectedBusinessId, expectedService);
    }

    @Test
    public void canDeleteBusinessService(long expectedBusinessId, long expectedServiceId) {
        willDoNothing().given(serviceService).deleteService(isA(Long.class), isA(Long.class));

        serviceController.deleteBusinessService(expectedBusinessId, expectedServiceId);

        verify(serviceService).deleteService(expectedBusinessId, expectedServiceId);
    }
}
