package com.ayds.zeday.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.ayds.zeday.domain.dto.schedule.GeneralScheduleDto;
import com.ayds.zeday.service.scheduling.ScheduleService;
import com.ayds.zeday.util.annotation.ZedayWebTest;
import com.ayds.zeday.util.paramresolver.ScheduleParamsResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

@ZedayWebTest
@WebMvcTest(ScheduleController.class)
@ExtendWith(ScheduleParamsResolver.class)
public class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ScheduleService scheduleService;

    @Test
    @WithMockUser
    public void canGetSchedules(long businessId, List<GeneralScheduleDto> expectedSchedules) throws Exception {
        given(scheduleService.findAllBusinessSchedules(businessId)).willReturn(expectedSchedules);

        ResultActions actualResult = mockMvc.perform(
                get("/api/schedules")
                        .header("CompanyId", businessId));

        actualResult.andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expectedSchedules)));
    }
}
