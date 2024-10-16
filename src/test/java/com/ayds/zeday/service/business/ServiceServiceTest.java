package com.ayds.zeday.service.business;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ayds.zeday.domain.dto.service.AddServiceDto;
import com.ayds.zeday.domain.dto.service.ServiceDto;
import com.ayds.zeday.domain.dto.service.UpdateServiceDto;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.ServiceEntity;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.BusinessRepository;
import com.ayds.zeday.repository.ServiceRepository;
import com.ayds.zeday.util.RandomUtils;
import com.ayds.zeday.util.annotation.ZedayTest;
import com.ayds.zeday.util.paramresolver.BusinessParamsResolver;
import com.ayds.zeday.util.paramresolver.ServiceParamsResolver;

@ZedayTest
@ExtendWith({ ServiceParamsResolver.class, BusinessParamsResolver.class })
public class ServiceServiceTest {

    private static final RandomUtils random = new RandomUtils();

    @Captor
    private ArgumentCaptor<ServiceEntity> serviceCaptor;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceService serviceService;

    @Test
    public void canFindBusinessServices(long businessId, List<ServiceDto> expectedServices) {
        given(serviceRepository.findAllByBusinessId(businessId, ServiceDto.class))
                .willReturn(expectedServices.stream().toList());

        List<ServiceDto> actualServices = serviceService.findBusinessServices(businessId);

        then(actualServices).isEqualTo(expectedServices);
    }

    @Test
    public void canFindService(long businessId, ServiceDto expectedService) {
        long serviceId = expectedService.id();

        given(serviceRepository.findByIdAndBusinessId(serviceId, businessId, ServiceDto.class))
                .willReturn(Optional.of(expectedService.toBuilder().build()));

        Optional<ServiceDto> actualService = serviceService.findService(businessId, serviceId);

        then(actualService).contains(expectedService);
    }

    @Test
    public void canFindServiceAndNotFoundIt(long businessId, long serviceId) {
        given(serviceRepository.findByIdAndBusinessId(serviceId, businessId, ServiceDto.class))
                .willReturn(Optional.empty());

        Optional<ServiceDto> actualBusiness = serviceService.findService(businessId, serviceId);

        then(actualBusiness).isEmpty();
    }

    @Test
    public void canAddService(BusinessEntity business, ServiceEntity expectedService) {
        long businessId = business.getId();
        expectedService = expectedService.toBuilder()
                .business(business)
                .build();

        AddServiceDto service = AddServiceDto.builder()
                .name(expectedService.getName())
                .description(expectedService.getDescription())
                .duration(expectedService.getDuration())
                .cancellable(Optional.of(expectedService.getCancellable()))
                .maxDaysToCancel(Optional.of(expectedService.getMaxDaysToCancel()))
                .minDaysToSchedule(Optional.of(expectedService.getMinDaysToSchedule()))
                .maxDaysToSchedule(Optional.of(expectedService.getMaxDaysToSchedule()))
                .advancePaymentPercentage(Optional.of(expectedService.getAdvancePaymentPercentage()))
                .build();

        given(businessRepository.findById(businessId)).willReturn(Optional.of(business));
        given(serviceRepository.existsByNameAndBusinessId(service.name(), businessId)).willReturn(false);
        given(serviceRepository.save(isA(ServiceEntity.class))).willReturn(expectedService.toBuilder()
                .id(random.nextPositiveLong())
                .build());

        serviceService.addService(businessId, service);

        verify(serviceRepository).save(serviceCaptor.capture());

        then(serviceCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedService);
    }

    @Test
    public void canBlockAddServiceWithBusinessNotFound(long businessId, AddServiceDto service) {
        given(businessRepository.findById(businessId)).willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> serviceService.addService(businessId, service));
    }

    @Test
    public void canBlockAddServiceWithDuplicateName(BusinessEntity business, AddServiceDto service) {
        long businessId = business.getId();

        given(businessRepository.findById(businessId)).willReturn(Optional.of(business));
        given(serviceRepository.existsByNameAndBusinessId(service.name(), businessId)).willReturn(true);

        assertThrows(RequestConflictException.class, () -> serviceService.addService(businessId, service));
    }

    private void canUpdateService(boolean updateDescription, boolean updateDuration, boolean updateCancellable,
            boolean updateMaxDaysToCancel, boolean updateMinDaysToSchedule, boolean updateMaxDaysToSchedule,
            boolean updateAdvancePaymentPercentage) {
        long businessId = random.nextPositiveLong();
        long serviceId = random.nextPositiveLong();
        String description = random.nextString();
        Duration duration = random.nextDuration();
        boolean cancellable = random.nextBoolean();
        int maxDaysToCancel = random.nextPositiveInt();
        int minDaysToSchedule = random.nextPositiveInt();
        int maxDaysToSchedule = random.nextPositiveInt();
        int advancePaymentPercentage = random.nextPositiveInt();

        BusinessEntity business = BusinessEntity.builder()
                .id(businessId)
                .name(random.nextString())
                .autoAssignment(random.nextBoolean())
                .build();

        ServiceEntity expectedService = ServiceEntity.builder()
                .id(serviceId)
                .name(random.nextString())
                .description(description)
                .duration(duration)
                .cancellable(cancellable)
                .maxDaysToCancel(maxDaysToCancel)
                .minDaysToSchedule(minDaysToSchedule)
                .maxDaysToSchedule(maxDaysToSchedule)
                .advancePaymentPercentage(advancePaymentPercentage)
                .business(business)
                .build();

        UpdateServiceDto service = UpdateServiceDto.builder()
                .description(Optional.ofNullable(updateDescription ? description : null))
                .duration(Optional.ofNullable(updateDuration ? duration : null))
                .cancellable(Optional.ofNullable(updateCancellable ? cancellable : null))
                .maxDaysToCancel(Optional.ofNullable(updateMaxDaysToCancel ? maxDaysToCancel : null))
                .minDaysToSchedule(Optional.ofNullable(updateMinDaysToSchedule ? minDaysToSchedule : null))
                .maxDaysToSchedule(Optional.ofNullable(updateMaxDaysToSchedule ? maxDaysToSchedule : null))
                .advancePaymentPercentage(
                        Optional.ofNullable(updateAdvancePaymentPercentage ? advancePaymentPercentage : null))
                .build();

        given(serviceRepository.findByIdAndBusinessId(isA(Long.class), isA(Long.class), eq(ServiceEntity.class)))
                .willReturn(Optional.of(expectedService.toBuilder()
                        .description(updateDescription ? random.nextString() : description)
                        .duration(updateDuration ? random.nextDuration() : duration)
                        .build()));

        given(serviceRepository.save(isA(ServiceEntity.class))).willReturn(expectedService.toBuilder().build());

        serviceService.updateService(businessId, serviceId, service);

        verify(serviceRepository).save(serviceCaptor.capture());

        then(serviceCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedService);
    }

    @Test
    public void canCompletlyUpdateService() {
        canUpdateService(true, true, true, true, true, true, true);
    }

    @Test
    public void canUpdateServiceDescription() {
        canUpdateService(true, false, false, false, false, false, false);
    }

    @Test
    public void canUpdateServiceDuration() {
        canUpdateService(false, true, false, false, false, false, false);
    }

    @Test
    public void canUpdateServiceCancellable() {
        canUpdateService(false, false, true, false, false, false, false);
    }

    @Test
    public void canUpdateServiceMaxDaysToCancel() {
        canUpdateService(false, false, false, true, false, false, false);
    }

    @Test
    public void canUpdateServiceMinDaysToSchedule() {
        canUpdateService(false, false, false, false, true, false, false);
    }

    @Test
    public void canUpdateServiceMaxDaysToSchedule() {
        canUpdateService(false, false, false, false, false, true, false);
    }

    @Test
    public void canUpdateServiceAdvancePaymentPercentage() {
        canUpdateService(false, false, false, false, false, false, true);
    }

    @Test
    public void canUpdateServiceStillWithoutChanges() {
        canUpdateService(false, false, false, false, false, false, false);
    }

    @Test
    public void canBlockUpdateServiceWithServiceNotFound(long businessId, long serviceId, UpdateServiceDto service) {
        given(businessRepository.findById(businessId)).willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> serviceService.updateService(businessId, serviceId, service));
    }

    @Test
    public void canDeleteService(long businessId, long serviceId) {
        willDoNothing().given(serviceRepository).deleteByIdAndBusinessId(isA(Long.class), isA(Long.class));

        serviceService.deleteService(businessId, serviceId);

        verify(serviceRepository).deleteByIdAndBusinessId(serviceId, businessId);
    }
}
