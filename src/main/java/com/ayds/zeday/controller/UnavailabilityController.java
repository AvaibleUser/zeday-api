package com.ayds.zeday.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.zeday.domain.dto.unavailability.AddUnavailabilityDto;
import com.ayds.zeday.domain.dto.unavailability.UpdateUnavailabilityDto;
import com.ayds.zeday.service.scheduling.UnavailabilityService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedules/{scheduleId}/unavailabilities")
@RequiredArgsConstructor
public class UnavailabilityController {

    private final UnavailabilityService unavailabilityService;

    @PostMapping
    @ResponseStatus(CREATED)
    public void createUnavailability(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId, @RequestBody @Valid AddUnavailabilityDto unavailability) {
        unavailabilityService.addScheduleUnavailability(businessId, scheduleId, unavailability);
    }

    @PutMapping("/{unavailabilityId}")
    @ResponseStatus(NO_CONTENT)
    public void updateUnavailability(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId, @PathVariable @Positive long unavailabilityId,
            @RequestBody @Valid UpdateUnavailabilityDto unavailability) {
        unavailabilityService.updateScheduleUnavailability(businessId, scheduleId, unavailabilityId, unavailability);
    }

    @DeleteMapping("/{unavailabilityId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteUnavailability(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId, @PathVariable @Positive long unavailabilityId) {
        unavailabilityService.removeScheduleUnavailability(businessId, scheduleId, unavailabilityId);
    }
}
