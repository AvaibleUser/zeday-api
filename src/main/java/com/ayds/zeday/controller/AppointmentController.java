package com.ayds.zeday.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.zeday.config.annotation.CurrentUserDto;
import com.ayds.zeday.domain.dto.appointment.AddAppointmentDto;
import com.ayds.zeday.domain.dto.appointment.UpdateAppointmentDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.service.scheduling.AppointmentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedules/{scheduleId}")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/services/{serviceId}/appointments")
    @ResponseStatus(CREATED)
    public void createScheduleAppointment(@CurrentUserDto UserDto customer,
            @RequestHeader("CompanyId") @Positive long businessId, @PathVariable @Positive long scheduleId,
            @PathVariable @Positive long serviceId, @RequestBody @Valid AddAppointmentDto appointment) {
        appointmentService.addScheduleAppointment(customer.getId(), businessId, scheduleId, serviceId, appointment);
    }

    @PutMapping("/appointments/{appointmentId}")
    @ResponseStatus(NO_CONTENT)
    public void updateScheduleAppointment(@RequestHeader("CompanyId") @Positive long businessId,
            @PathVariable @Positive long scheduleId, @PathVariable @Positive long appointmentId,
            @RequestBody @Valid UpdateAppointmentDto appointment) {
        appointmentService.updateScheduledAppointment(appointmentId, businessId, scheduleId, appointment);
    }
}
