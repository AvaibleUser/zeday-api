package com.ayds.zeday.service.business;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.zeday.domain.dto.service.AddServiceDto;
import com.ayds.zeday.domain.dto.service.ServiceDto;
import com.ayds.zeday.domain.dto.service.UpdateServiceDto;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.ServiceEntity;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.BusinessRepository;
import com.ayds.zeday.repository.ServiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;

    public List<ServiceDto> findBusinessServices(long businessId) {
        return serviceRepository.findAllByBusinessId(businessId, ServiceDto.class);
    }

    public Optional<ServiceDto> findService(long businessId, long serviceId) {
        return serviceRepository.findByIdAndBusinessId(serviceId, businessId, ServiceDto.class);
    }

    @Transactional
    public void addService(long businessId, AddServiceDto service) {
        BusinessEntity business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la compañia actual"));

        if (serviceRepository.existsByNameAndBusinessId(service.name(), businessId)) {
            throw new RequestConflictException("El nombre del servicio que se intenta registrar ya esta en uso");
        }

        ServiceEntity newService = ServiceEntity.builder()
                .name(service.name())
                .description(service.description())
                .duration(service.duration())
                .price(service.price())
                .cancellable(service.cancellable().orElse(false))
                .maxDaysToCancel(service.maxDaysToCancel().orElse(Integer.MAX_VALUE))
                .minDaysToSchedule(service.minDaysToSchedule().orElse(Integer.MAX_VALUE))
                .maxDaysToSchedule(service.maxDaysToSchedule().orElse(Integer.MAX_VALUE))
                .advancePaymentPercentage(service.advancePaymentPercentage().orElse(Integer.MAX_VALUE))
                .business(business)
                .build();

        serviceRepository.save(newService);
    }

    @Transactional
    public void updateService(long businessId, long serviceId, UpdateServiceDto service) {
        ServiceEntity dbService = serviceRepository.findByIdAndBusinessId(serviceId, businessId, ServiceEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar el servicio"));

        service.description()
                .filter(ObjectUtils::isNotEmpty)
                .ifPresent(dbService::setDescription);

        service.duration()
                .ifPresent(dbService::setDuration);

        service.cancellable()
                .ifPresent(dbService::setCancellable);

        service.maxDaysToCancel()
                .ifPresent(dbService::setMaxDaysToCancel);

        service.minDaysToSchedule()
                .ifPresent(dbService::setMinDaysToSchedule);

        service.maxDaysToSchedule()
                .ifPresent(dbService::setMaxDaysToSchedule);

        service.advancePaymentPercentage()
                .ifPresent(dbService::setAdvancePaymentPercentage);

        serviceRepository.save(dbService);
    }

    public void deleteService(long businessId, long serviceId) {
        serviceRepository.deleteByIdAndBusinessId(serviceId, businessId);
    }
}
