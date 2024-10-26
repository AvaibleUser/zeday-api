package com.ayds.zeday.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.zeday.domain.dto.schedule.AddScheduleDto;
import com.ayds.zeday.domain.dto.schedule.GeneralScheduleDto;
import com.ayds.zeday.domain.dto.schedule.ScheduleDto;
import com.ayds.zeday.domain.dto.schedule.UpdateScheduleDto;
import com.ayds.zeday.service.scheduling.ScheduleService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<GeneralScheduleDto>> getSchedules(
            @RequestHeader("CompanyId") @Positive long businessId) {
        List<GeneralScheduleDto> schedules = scheduleService.findAllBusinessSchedules(businessId);

        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/services/{serviceIds}")
    public ResponseEntity<List<ScheduleDto>> getSchedulesByService(
            @RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @NotEmpty List<@Positive Long> serviceIds,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate from,
            @RequestParam(defaultValue = "false") boolean week) {
        List<ScheduleDto> schedules = scheduleService.findAllBusinessSchedulesByService(businessId, from, from,
                serviceIds);

        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDto> getSchedule(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate from,
            @RequestParam(defaultValue = "false") boolean week) {
        LocalDate to = week ? from.plusWeeks(1) : from;
        Optional<ScheduleDto> schedule = scheduleService.findBusinessSchedule(businessId, scheduleId, from, to);

        return ResponseEntity.of(schedule);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public void createSchedule(@RequestHeader("CompanyId") @Positive long businessId,
            @RequestBody @Valid AddScheduleDto schedule) {
        scheduleService.addBusinessSchedule(businessId, schedule);
    }

    @PutMapping("/{scheduleId}")
    @ResponseStatus(NO_CONTENT)
    public void updateSchedule(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId, @RequestBody @Valid UpdateScheduleDto schedule) {
        scheduleService.updateBusinessSchedule(businessId, scheduleId, schedule);
    }

    @PutMapping("/{scheduleId}/services/{serviceIds}")
    @ResponseStatus(NO_CONTENT)
    public void toggleServicesIntoSchedule(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId, @PathVariable @NotEmpty List<@Positive Long> serviceId) {
        scheduleService.toggleServicesToBusinessSchedule(businessId, scheduleId, serviceId);
    }
}
