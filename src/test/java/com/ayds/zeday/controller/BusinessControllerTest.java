package com.ayds.zeday.controller;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.ayds.zeday.domain.dto.business.AddBusinessDto;
import com.ayds.zeday.domain.dto.business.BusinessDto;
import com.ayds.zeday.domain.dto.business.BusinessIdDto;
import com.ayds.zeday.domain.dto.business.BusinessLogoDto;
import com.ayds.zeday.domain.dto.business.UpdateBusinessDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.service.business.BusinessService;
import com.ayds.zeday.service.util.FileStorageService;
import com.ayds.zeday.util.RandomUtils;
import com.ayds.zeday.util.annotation.ZedayTest;
import com.ayds.zeday.util.paramresolver.BusinessParamsResolver;
import com.ayds.zeday.util.paramresolver.UserParamsResolver;

@ZedayTest
@ExtendWith({ BusinessParamsResolver.class, UserParamsResolver.class })
public class BusinessControllerTest {

    private static final RandomUtils random = new RandomUtils();

    @MockBean
    private BusinessService businessService;

    @MockBean
    private FileStorageService fileStorageService;

    @Autowired
    private BusinessController businessController;

    private BusinessDto addLogoUrl(BusinessDto business) {
        return business.toBuilder()
                .logoUrl(random.nextString())
                .build();
    }

    @Test
    public void canGetCurrentBusiness(BusinessDto expectedBusiness) {
        long businessId = expectedBusiness.id();
        expectedBusiness = addLogoUrl(expectedBusiness);

        given(businessService.findBusiness(businessId))
                .willReturn(Optional.of(expectedBusiness.toBuilder().build()));

        ResponseEntity<BusinessDto> actualBusiness = businessController.getCurrentBusiness(businessId);

        then(actualBusiness)
                .extracting(ResponseEntity::getBody)
                .isEqualTo(expectedBusiness);

        then(actualBusiness)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(OK);
    }

    @Test
    public void canNotFoundCurrentBusiness(long businessId) {
        given(businessService.findBusiness(businessId)).willReturn(Optional.empty());

        ResponseEntity<BusinessDto> actualBusiness = businessController.getCurrentBusiness(businessId);

        then(actualBusiness)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(NOT_FOUND);
    }

    @Test
    public void canCreateBusiness(long businessId, AddBusinessDto business, UserDto user) {
        BusinessIdDto expectedBusiness = new BusinessIdDto(businessId);

        given(businessService.addBusiness(user.getId(), business.toBuilder().build()))
                .willReturn(businessId);

        ResponseEntity<BusinessIdDto> actualBusiness = businessController.createBusiness(user, business);

        then(actualBusiness)
                .extracting(ResponseEntity::getBody)
                .isEqualTo(expectedBusiness);

        then(actualBusiness)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(CREATED);
    }

    @Test
    public void canUpdateBusiness(long expectedBusinessId, UpdateBusinessDto expectedBusiness) {
        willDoNothing().given(businessService).updateBusiness(isA(Long.class), isA(UpdateBusinessDto.class));

        businessController.updateBusiness(expectedBusinessId, expectedBusiness.toBuilder().build());

        verify(businessService).updateBusiness(expectedBusinessId, expectedBusiness);
    }

    @Test
    public void canNotFoundCurrentBusinessForAddBusinessLogo(long businessId, String filename, String content) {
        MockMultipartFile logo = new MockMultipartFile(filename, content.getBytes());

        given(businessService.findBusiness(isA(Long.class))).willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class,
                () -> businessController.updateBusinessLogo(businessId, logo));

        verify(businessService).findBusiness(businessId);
    }

    @Test
    public void canAddBusinessLogo(String logoPath, String filename, String content, BusinessDto business) {
        long businessId = business.id();
        MockMultipartFile logo = new MockMultipartFile(filename, content.getBytes());
        BusinessLogoDto expectedBusiness = new BusinessLogoDto(businessId, logoPath);

        given(businessService.findBusiness(businessId)).willReturn(Optional.of(business.toBuilder().build()));
        given(fileStorageService.store(contains(String.valueOf(businessId)), eq(logo)))
                .willReturn(logoPath);

        willDoNothing().given(businessService).addImageToBusiness(isA(Long.class), isA(String.class));

        ResponseEntity<BusinessLogoDto> actualBusiness = businessController.updateBusinessLogo(businessId, logo);

        verify(businessService).addImageToBusiness(businessId, logoPath);

        then(actualBusiness)
                .extracting(ResponseEntity::getBody)
                .isEqualTo(expectedBusiness);

        then(actualBusiness)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(OK);
    }

    @Test
    public void canUpdateBusinessLogo(String logoPath, String filename, BusinessDto business) {
        long businessId = business.id();
        MockMultipartFile logo = new MockMultipartFile(filename, random.nextString().getBytes());
        BusinessLogoDto expectedBusiness = new BusinessLogoDto(businessId, logoPath);

        given(businessService.findBusiness(businessId)).willReturn(Optional.of(addLogoUrl(business)));
        given(fileStorageService.store(contains(String.valueOf(businessId)), eq(logo)))
                .willReturn(logoPath);

        ResponseEntity<BusinessLogoDto> actualBusiness = businessController.updateBusinessLogo(businessId,
                logo);

        verify(businessService, never()).addImageToBusiness(anyLong(), any());

        then(actualBusiness)
                .extracting(ResponseEntity::getBody)
                .isEqualTo(expectedBusiness);

        then(actualBusiness)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(OK);
    }
}
