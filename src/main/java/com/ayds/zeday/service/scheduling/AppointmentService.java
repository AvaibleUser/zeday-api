package com.ayds.zeday.service.scheduling;

import static java.util.function.Predicate.not;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.zeday.domain.dto.appointment.AddAppointmentDto;
import com.ayds.zeday.domain.dto.appointment.UpdateAppointmentDto;
import com.ayds.zeday.domain.entity.AppointmentEntity;
import com.ayds.zeday.domain.entity.ScheduleEntity;
import com.ayds.zeday.domain.entity.ServiceEntity;
import com.ayds.zeday.domain.entity.UserEntity;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.AppointmentRepository;
import com.ayds.zeday.repository.ScheduleRepository;
import com.ayds.zeday.repository.ServiceRepository;
import com.ayds.zeday.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addScheduleAppointment(long customerId, long businessId, long scheduleId, long serviceId,
            AddAppointmentDto appointment) {
        ScheduleEntity schedule = scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la compañia actual o el horario"));

        ServiceEntity service = serviceRepository.findByIdAndBusinessId(serviceId, businessId, ServiceEntity.class)
                .filter(not(serv -> schedule.getServices().contains(serv)))
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el servicio"));

        UserEntity customer = userRepository.findByIdAndBusinessId(customerId, businessId, UserEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el usuario"));

        UserEntity attendant = userRepository
                .findByIdAndBusinessId(appointment.attendant(), businessId, UserEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro el usuario"));

        if (userRepository.existsByIdAndBusinessIdAndWithScheduledAppointmentBetweenDates(customerId,
                appointment.startAt(), appointment.endAt())) {
            throw new RequestConflictException("El cliente ya tiene una cita agendada en esas horas");
        }
        if (userRepository.existsByIdAndBusinessIdAndWithAssignedAppointmentBetweenDates(appointment.attendant(),
                appointment.startAt(), appointment.endAt())) {
            throw new RequestConflictException("El encargado ya tiene asignada una cita a esa hora");
        }

        appointmentRepository.save(AppointmentEntity.builder()
                .startAt(appointment.startAt())
                .endAt(appointment.endAt())
                .customer(customer)
                .schedule(schedule)
                .service(service)
                .attendant(attendant)
                .build());
    }

    @Transactional
    public void updateScheduledAppointment(long appointmentId, long businessId, long scheduleId,
            UpdateAppointmentDto appointment) {
        scheduleRepository.findByIdAndBusinessId(scheduleId, businessId, ScheduleEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la compañia actual o el horario"));

        AppointmentEntity dbAppointment = appointmentRepository
                .findByIdAndScheduleId(appointmentId, scheduleId, AppointmentEntity.class)
                .orElseThrow(() -> new ValueNotFoundException("No se encontro la cita"));

        appointment.state().ifPresent(dbAppointment::setState);
        appointment.notes().ifPresent(dbAppointment::setNotes);

        appointmentRepository.save(dbAppointment);
    }
}
