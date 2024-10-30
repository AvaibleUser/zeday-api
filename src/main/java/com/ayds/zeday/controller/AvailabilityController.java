package com.ayds.zeday.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.zeday.domain.dto.availability.AddAvailabilityDto;
import com.ayds.zeday.service.scheduling.AvailabilityService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedules/{scheduleId}/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    @ResponseStatus(CREATED)
    public void createAvailability(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId, @RequestBody @Valid AddAvailabilityDto availability) {
        availabilityService.addScheduleAvailability(businessId, scheduleId, availability);
    }

    @PutMapping
    @ResponseStatus(NO_CONTENT)
    public void updateAvailability(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId, @RequestBody @Valid List<AddAvailabilityDto> availabilities) {
        availabilityService.updateScheduleUnavailability(businessId, scheduleId, availabilities);
    }

    @DeleteMapping("/{availabilityId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteAvailability(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId, @PathVariable @Positive long availabilityId) {
        availabilityService.removeScheduleAvailability(businessId, scheduleId, availabilityId);
    }
}
