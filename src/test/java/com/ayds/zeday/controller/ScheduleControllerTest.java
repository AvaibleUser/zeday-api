package com.ayds.zeday.controller;

import static java.util.stream.Collectors.joining;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.ayds.zeday.domain.dto.schedule.AddScheduleDto;
import com.ayds.zeday.domain.dto.schedule.GeneralScheduleDto;
import com.ayds.zeday.domain.dto.schedule.ScheduleDto;
import com.ayds.zeday.domain.dto.schedule.UpdateScheduleDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.service.scheduling.ScheduleService;
import com.ayds.zeday.util.annotation.ZedayWebTest;
import com.ayds.zeday.util.paramresolver.ScheduleParamsResolver;
import com.ayds.zeday.util.paramresolver.UserParamsResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

@ZedayWebTest
@ExtendWith({ ScheduleParamsResolver.class, UserParamsResolver.class })
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ScheduleService scheduleService;

    @Test
    public void canGetSchedules(long businessId, List<GeneralScheduleDto> expectedSchedules) throws Exception {
        given(scheduleService.findAllBusinessSchedules(businessId)).willReturn(expectedSchedules.stream().toList());

        ResultActions actualResult = mockMvc.perform(
                get("/api/schedules")
                        .header("CompanyId", businessId));

        actualResult.andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expectedSchedules)));
    }

    @Test
    public void canGetSchedulesByServicePerWeek(long businessId, List<ScheduleDto> expectedSchedules, LocalDate from,
            List<Long> serviceIds) throws Exception {
        LocalDate to = from.plusWeeks(1);

        given(scheduleService.findAllBusinessSchedulesByService(businessId, from, to, serviceIds))
                .willReturn(expectedSchedules.stream().toList());

        ResultActions actualResult = mockMvc.perform(
                get("/api/schedules/services/{serviceIds}",
                        serviceIds.stream()
                                .map(String::valueOf)
                                .collect(joining(",")))
                        .header("CompanyId", businessId)
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .param("week", "true"));

        actualResult.andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expectedSchedules)));
    }

    @Test
    public void canGetSchedulesByServicePerDay(long businessId, List<ScheduleDto> expectedSchedules, LocalDate from,
            List<Long> serviceIds) throws Exception {
        LocalDate to = from;

        given(scheduleService.findAllBusinessSchedulesByService(businessId, from, to, serviceIds))
                .willReturn(expectedSchedules.stream().toList());

        ResultActions actualResult = mockMvc.perform(
                get("/api/schedules/services/{serviceIds}",
                        serviceIds.stream()
                                .map(String::valueOf)
                                .collect(joining(",")))
                        .header("CompanyId", businessId)
                        .param("from", from.toString())
                        .param("week", "false"));

        actualResult.andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expectedSchedules)));
    }

    @Test
    public void canGetSchedulesPerWeek(long businessId, ScheduleDto expectedSchedule, LocalDate from) throws Exception {
        long scheduleId = expectedSchedule.id();
        LocalDate to = from.plusWeeks(1);

        given(scheduleService.findBusinessSchedule(businessId, scheduleId, from, to))
                .willReturn(Optional.of(expectedSchedule.toBuilder().build()));

        ResultActions actualResult = mockMvc.perform(
                get("/api/schedules/{scheduleId}", scheduleId)
                        .header("CompanyId", businessId)
                        .param("from", from.toString())
                        .param("week", "true"));

        actualResult.andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expectedSchedule)));
    }

    @Test
    public void canGetSchedulesPerDay(long businessId, ScheduleDto expectedSchedule, LocalDate from) throws Exception {
        long scheduleId = expectedSchedule.id();
        LocalDate to = from;

        given(scheduleService.findBusinessSchedule(businessId, scheduleId, from, to))
                .willReturn(Optional.of(expectedSchedule.toBuilder().build()));

        ResultActions actualResult = mockMvc.perform(
                get("/api/schedules/{scheduleId}", scheduleId)
                        .header("CompanyId", businessId)
                        .param("from", from.toString())
                        .param("week", "false"));

        actualResult.andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expectedSchedule)));
    }

    @Test
    @Disabled
    public void canGetPossibleAttendants(long businessId, long scheduleId, long serviceId, Instant from, Instant to,
            List<UserDto> expectedUsers) throws Exception {
        given(scheduleService.findPossibleAttendantInBusinessSchedule(businessId, scheduleId, serviceId, from, to))
                .willReturn(expectedUsers.stream().toList());

        ResultActions actualResult = mockMvc.perform(
                get("/api/schedules/{scheduleId}/services/{serviceId}/attendants", scheduleId, serviceId)
                        .header("CompanyId", businessId)
                        .param("from", from.toString())
                        .param("to", to.toString()));

        actualResult.andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expectedUsers)));
    }

    @Test
    public void canCreateSchedule(long businessId, AddScheduleDto business) throws Exception {
        willDoNothing().given(scheduleService).addBusinessSchedule(isA(Long.class), isA(AddScheduleDto.class));

        ResultActions actualResult = mockMvc.perform(
                post("/api/schedules")
                        .header("CompanyId", businessId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(business)));

        verify(scheduleService).addBusinessSchedule(businessId, business);
        actualResult.andExpect(status().isCreated());
    }

    @Test
    public void canUpdateSchedule(long businessId, long scheduleId, UpdateScheduleDto business) throws Exception {
        willDoNothing().given(scheduleService).updateBusinessSchedule(isA(Long.class), isA(Long.class),
                isA(UpdateScheduleDto.class));

        ResultActions actualResult = mockMvc.perform(
                put("/api/schedules/{scheduleId}", scheduleId)
                        .header("CompanyId", businessId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(business)));

        verify(scheduleService).updateBusinessSchedule(businessId, scheduleId, business);
        actualResult.andExpect(status().isNoContent());
    }

    @Test
    public void canToggleServicesIntoSchedule(long businessId, long scheduleId, List<Long> serviceIds)
            throws Exception {
        willDoNothing().given(scheduleService).toggleServicesToBusinessSchedule(isA(Long.class), isA(Long.class),
                anyList());

        ResultActions actualResult = mockMvc.perform(
                put("/api/schedules/{scheduleId}/services/{serviceIds}", scheduleId,
                        serviceIds.stream()
                                .map(String::valueOf)
                                .collect(joining(",")))
                        .header("CompanyId", businessId)
                        .contentType(APPLICATION_JSON));

        verify(scheduleService).toggleServicesToBusinessSchedule(businessId, scheduleId, serviceIds);
        actualResult.andExpect(status().isNoContent());
    }
}
