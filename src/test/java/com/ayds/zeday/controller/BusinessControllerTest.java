package com.ayds.zeday.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.ayds.zeday.domain.dto.business.AddBusinessDto;
import com.ayds.zeday.domain.dto.business.BusinessDto;
import com.ayds.zeday.domain.dto.business.BusinessIdDto;
import com.ayds.zeday.domain.dto.business.BusinessLogoDto;
import com.ayds.zeday.domain.dto.business.UpdateBusinessDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.repository.UserRepository;
import com.ayds.zeday.service.business.BusinessService;
import com.ayds.zeday.service.util.FileStorageService;
import com.ayds.zeday.util.RandomUtils;
import com.ayds.zeday.util.annotation.ZedayWebTest;
import com.ayds.zeday.util.paramresolver.BusinessParamsResolver;
import com.ayds.zeday.util.paramresolver.UserParamsResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ZedayWebTest
@ExtendWith({ BusinessParamsResolver.class, UserParamsResolver.class })
public class BusinessControllerTest {

    private static final RandomUtils random = new RandomUtils();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BusinessService businessService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private FileStorageService fileStorageService;

    private BusinessDto addLogoUrl(BusinessDto business) {
        return business.toBuilder()
                .logoUrl(random.nextString())
                .build();
    }

    @Test
    public void canGetCurrentBusiness(BusinessDto expectedBusiness) throws Exception {
        long businessId = expectedBusiness.id();
        expectedBusiness = addLogoUrl(expectedBusiness);

        given(businessService.findBusiness(businessId))
                .willReturn(Optional.of(expectedBusiness.toBuilder().build()));

        ResultActions actualResult = mockMvc.perform(
                get("/api/companies/current")
                        .header("CompanyId", businessId));

        actualResult.andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expectedBusiness)));
    }

    @Test
    public void canNotFoundCurrentBusiness(long businessId) throws Exception {
        given(businessService.findBusiness(businessId)).willReturn(Optional.empty());

        ResultActions actualResult = mockMvc.perform(
                get("/api/companies/current")
                        .header("CompanyId", businessId));

        actualResult.andExpect(status().isNotFound());
    }

    @Test
    public void canCreateBusiness(long businessId, AddBusinessDto business, UserDto user)
            throws JsonProcessingException, Exception {
        BusinessIdDto expectedBusiness = new BusinessIdDto(businessId);

        given(userRepository.findUserDtoById(user.getId())).willReturn(Optional.of(user));
        given(businessService.addBusiness(user.getId(), business.toBuilder().build()))
                .willReturn(businessId);

        ResultActions actualResult = mockMvc.perform(
                post("/api/companies")
                        .accept(APPLICATION_JSON)
                        .header("CompanyId", businessId)
                        .with(jwt().jwt(builder -> builder.subject(String.valueOf(user.getId()))))
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(business)));

        actualResult.andExpect(status().isCreated())
                .andExpect(content().string(mapper.writeValueAsString(expectedBusiness)));
    }

    @Test
    public void canUpdateBusiness(long expectedBusinessId, UpdateBusinessDto expectedBusiness)
            throws JsonProcessingException, Exception {
        willDoNothing().given(businessService).updateBusiness(isA(Long.class), isA(UpdateBusinessDto.class));

        ResultActions actualResult = mockMvc.perform(
                put("/api/companies")
                        .accept(APPLICATION_JSON)
                        .header("CompanyId", expectedBusinessId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(expectedBusiness)));

        verify(businessService).updateBusiness(expectedBusinessId, expectedBusiness);
        actualResult.andExpect(status().isNoContent());
    }

    @Test
    public void canAddBusinessLogo(String logoPath, String filename, String content, BusinessDto business)
            throws Exception {
        long businessId = business.id();
        MockMultipartFile logo = new MockMultipartFile("logo", filename, IMAGE_JPEG_VALUE, content.getBytes());
        BusinessLogoDto expectedBusiness = new BusinessLogoDto(businessId, logoPath);

        given(businessService.findBusiness(businessId)).willReturn(Optional.of(business.toBuilder().build()));
        given(fileStorageService.store(contains(String.valueOf(businessId)), eq(logo)))
                .willReturn(logoPath);

        willDoNothing().given(businessService).addImageToBusiness(isA(Long.class), isA(String.class));

        ResultActions actualResult = mockMvc.perform(
                multipart(PATCH, "/api/companies")
                        .file(logo)
                        .header("CompanyId", businessId));

        verify(businessService).addImageToBusiness(businessId, logoPath);
        actualResult.andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expectedBusiness)));
    }

    @Test
    public void canUpdateBusinessLogo(String logoPath, String filename, String content, BusinessDto business)
            throws Exception {
        long businessId = business.id();
        MockMultipartFile logo = new MockMultipartFile("logo", filename, IMAGE_JPEG_VALUE, content.getBytes());
        BusinessLogoDto expectedBusiness = new BusinessLogoDto(businessId, logoPath);

        given(businessService.findBusiness(businessId)).willReturn(Optional.of(addLogoUrl(business)));
        given(fileStorageService.store(contains(String.valueOf(businessId)), eq(logo)))
                .willReturn(logoPath);

        willDoNothing().given(businessService).addImageToBusiness(isA(Long.class), isA(String.class));

        ResultActions actualResult = mockMvc.perform(
                multipart(PATCH, "/api/companies")
                        .file(logo)
                        .header("CompanyId", businessId));

        verify(businessService).addImageToBusiness(businessId, logoPath);
        actualResult.andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expectedBusiness)));
    }

    @Test
    public void canNotFoundCurrentBusinessForAddBusinessLogo(long businessId, String filename, String content)
            throws Exception {
        MockMultipartFile logo = new MockMultipartFile("logo", filename, IMAGE_JPEG_VALUE, content.getBytes());

        given(businessService.findBusiness(isA(Long.class))).willReturn(Optional.empty());

        ResultActions actualResult = mockMvc.perform(
                multipart(PATCH, "/api/companies")
                        .file(logo)
                        .header("CompanyId", businessId));

        verify(businessService).findBusiness(businessId);
        verify(businessService, never()).addImageToBusiness(anyLong(), any());
        actualResult.andExpect(status().isNotFound());
    }
}
