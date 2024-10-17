package com.ayds.zeday.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.zeday.domain.dto.availability.AddAvailabilityDto;
import com.ayds.zeday.domain.dto.availability.UpdateAvailabilityDto;
import com.ayds.zeday.service.scheduling.AvailabilityService;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedules/{scheduleId}/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    public void createAvailability(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId, @RequestBody AddAvailabilityDto availability) {
        availabilityService.addScheduleAvailability(businessId, scheduleId, availability);
    }

    @PutMapping("/{availabilityId}")
    public void updateAvailability(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId, @RequestBody UpdateAvailabilityDto availability) {
        availabilityService.updateScheduleUnavailability(businessId, scheduleId, scheduleId, availability);
    }

    @DeleteMapping("/{availabilityId}")
    public void deleteAvailability(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId, @PathVariable @Positive long availabilityId) {
        availabilityService.removeScheduleAvailability(businessId, scheduleId, availabilityId);
    }
}
