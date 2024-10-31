package com.ayds.zeday.controller;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.ayds.zeday.domain.dto.unavailability.AddUnavailabilityDto;
import com.ayds.zeday.domain.dto.unavailability.UpdateUnavailabilityDto;
import com.ayds.zeday.service.scheduling.UnavailabilityService;
import com.ayds.zeday.util.annotation.ZedayWebTest;
import com.ayds.zeday.util.paramresolver.UnavailabilityParamsResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

@ZedayWebTest
@ExtendWith({ UnavailabilityParamsResolver.class })
public class UnavailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UnavailabilityService unavailabilityService;

    @Test
    public void canCreateUnavailability(long businessId, long scheduleId, long unavailabilityId,
            AddUnavailabilityDto unavailability) throws Exception {
        willDoNothing().given(unavailabilityService).addScheduleUnavailability(businessId, scheduleId, unavailability);

        ResultActions actualResults = mockMvc.perform(
                post("/api/schedules/{scheduleId}/unavailabilities", scheduleId)
                        .header("CompanyId", businessId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(unavailability)));

        verify(unavailabilityService).addScheduleUnavailability(businessId, scheduleId, unavailability);
        actualResults.andExpect(status().isCreated());
    }

    @Test
    public void canUpdateUnavailability(long businessId, long scheduleId, long unavailabilityId,
            UpdateUnavailabilityDto unavailability) throws Exception {
        willDoNothing().given(unavailabilityService).updateScheduleUnavailability(businessId, scheduleId,
                unavailabilityId, unavailability);

        ResultActions actualResults = mockMvc.perform(
                put("/api/schedules/{scheduleId}/unavailabilities/{unavailabilityId}", scheduleId, unavailabilityId)
                        .header("CompanyId", businessId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(unavailability)));

        verify(unavailabilityService).updateScheduleUnavailability(businessId, scheduleId, unavailabilityId,
                unavailability);
        actualResults.andExpect(status().isNoContent());
    }

    @Test
    public void canDeleteUnavailability(long businessId, long scheduleId, long unavailabilityId) throws Exception {
        willDoNothing().given(unavailabilityService).removeScheduleUnavailability(businessId, scheduleId,
                unavailabilityId);

        ResultActions actualResults = mockMvc.perform(
                delete("/api/schedules/{scheduleId}/unavailabilities/{unavailabilityId}", scheduleId, unavailabilityId)
                        .header("CompanyId", businessId));

        verify(unavailabilityService).removeScheduleUnavailability(businessId, scheduleId, unavailabilityId);
        actualResults.andExpect(status().isNoContent());
    }
}
