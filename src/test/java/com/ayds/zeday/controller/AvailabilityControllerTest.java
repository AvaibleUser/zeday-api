package com.ayds.zeday.controller;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.ayds.zeday.domain.dto.availability.AddAvailabilityDto;
import com.ayds.zeday.service.scheduling.AvailabilityService;
import com.ayds.zeday.util.annotation.ZedayWebTest;
import com.ayds.zeday.util.paramresolver.AvailabilityParamsResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

@ZedayWebTest
@ExtendWith({ AvailabilityParamsResolver.class })
public class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AvailabilityService availabilityService;

    @Test
    public void canCreateAvailability(long businessId, long scheduleId, AddAvailabilityDto availability)
            throws Exception {
        willDoNothing().given(availabilityService).addScheduleAvailability(businessId, scheduleId, availability);

        ResultActions actualResults = mockMvc.perform(
                post("/api/schedules/{scheduleId}/availabilities", scheduleId)
                        .header("CompanyId", businessId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(availability)));

        verify(availabilityService).addScheduleAvailability(businessId, scheduleId, availability);
        actualResults.andExpect(status().isCreated());
    }

    @Test
    public void canUpdateAvailability(long businessId, long scheduleId, List<AddAvailabilityDto> availabilities)
            throws Exception {
        willDoNothing().given(availabilityService).updateScheduleUnavailability(businessId, scheduleId, availabilities);

        ResultActions actualResults = mockMvc.perform(
                put("/api/schedules/{scheduleId}/availabilities", scheduleId)
                        .header("CompanyId", businessId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(availabilities)));

        verify(availabilityService).updateScheduleUnavailability(businessId, scheduleId, availabilities);
        actualResults.andExpect(status().isNoContent());
    }

    @Test
    public void canDeleteAvailability(long businessId, long scheduleId, long availabilityId) throws Exception {
        willDoNothing().given(availabilityService).removeScheduleAvailability(businessId, scheduleId, availabilityId);

        ResultActions actualResults = mockMvc.perform(
                delete("/api/schedules/{scheduleId}/availabilities/{availabilityId}", scheduleId, availabilityId)
                        .header("CompanyId", businessId));

        verify(availabilityService).removeScheduleAvailability(businessId, scheduleId, availabilityId);
        actualResults.andExpect(status().isNoContent());
    }
}
