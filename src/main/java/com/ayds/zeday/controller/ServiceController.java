package com.ayds.zeday.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.zeday.domain.dto.service.AddServiceDto;
import com.ayds.zeday.domain.dto.service.ServiceDto;
import com.ayds.zeday.domain.dto.service.UpdateServiceDto;
import com.ayds.zeday.service.business.ServiceService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<ServiceDto>> getBusinessServices(@RequestHeader("CompanyId") @Positive long businessId) {
        List<ServiceDto> businessServices = serviceService.findBusinessServices(businessId);

        return ResponseEntity.ok(businessServices);
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<ServiceDto> getBusinessService(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long serviceId) {
        Optional<ServiceDto> service = serviceService.findService(businessId, serviceId);

        return ResponseEntity.of(service);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public void createBusinessService(@RequestHeader("CompanyId") @Positive long businessId,
            @RequestBody @Valid AddServiceDto service) {
        serviceService.addService(businessId, service);
    }

    @PutMapping("/{serviceId}")
    @ResponseStatus(NO_CONTENT)
    public void updateBusinessService(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable("serviceId") @Positive long serviceId, @RequestBody @Valid UpdateServiceDto service) {
        serviceService.updateService(businessId, serviceId, service);
    }

    @DeleteMapping("/{serviceId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteBusinessService(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long serviceId) {
        serviceService.deleteService(businessId, serviceId);
    }
}
