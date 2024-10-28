package com.ayds.zeday.service.business;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ayds.zeday.domain.dto.business.AddBusinessDto;
import com.ayds.zeday.domain.dto.business.BusinessDto;
import com.ayds.zeday.domain.dto.business.UpdateBusinessDto;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.entity.UserEntity;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.BusinessRepository;
import com.ayds.zeday.repository.UserRepository;
import com.ayds.zeday.util.annotation.ZedayTest;
import com.ayds.zeday.util.paramresolver.BusinessParamsResolver;
import com.ayds.zeday.util.paramresolver.UserParamsResolver;

@ZedayTest
@ExtendWith({ BusinessParamsResolver.class, UserParamsResolver.class })
public class BusinessServiceTest {

    @Captor
    private ArgumentCaptor<BusinessEntity> businessCaptor;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private ConcurrentMap<Long, ConcurrentMap<String, String>> emailConfirmationCodes;

    @Autowired
    private BusinessService businessService;

    @Test
    public void canFindBusiness(BusinessDto expectedBusiness) {
        long businessId = expectedBusiness.id();

        given(businessRepository.findBusinessById(businessId))
                .willReturn(Optional.of(expectedBusiness.toBuilder().build()));

        Optional<BusinessDto> actualBusiness = businessService.findBusiness(businessId);

        then(actualBusiness).contains(expectedBusiness);
    }

    @Test
    public void canFindBusinessAndNotFoundIt(long businessId) {
        given(businessRepository.findBusinessById(businessId)).willReturn(Optional.empty());

        Optional<BusinessDto> actualBusiness = businessService.findBusiness(businessId);

        then(actualBusiness).isEmpty();
    }

    @Test
    public void canAddBusiness(BusinessEntity expectedBusiness, UserEntity expectedUser) {
        long expectedBusinessId = expectedBusiness.getId();
        long expectedUserId = expectedUser.getId();
        AddBusinessDto businessDto = AddBusinessDto.builder()
                .name(expectedBusiness.getName())
                .autoAssignment(Optional.ofNullable(expectedBusiness.getAutoAssignment()))
                .build();

        BusinessEntity business = expectedBusiness.toBuilder()
                .id(null)
                .logoUrl(null)
                .build();

        given(emailConfirmationCodes.put(isA(Long.class), any())).willReturn(null);
        given(businessRepository.existsByName(businessDto.name())).willReturn(false);
        given(businessRepository.saveAndFlush(business))
                .willReturn(expectedBusiness.toBuilder().build());
        given(userRepository.findByIdAndBusinessId(isA(Long.class), isA(Long.class), any()))
                .willAnswer(invocation -> Optional.of(expectedUser.toBuilder().build()));

        long actualBusinessId = businessService.addBusiness(expectedUserId, businessDto);

        verify(userRepository).findByIdAndBusinessId(expectedUserId, 1L, UserEntity.class);
        verify(emailConfirmationCodes).put(expectedBusinessId, new ConcurrentHashMap<>());
        then(actualBusinessId)
                .usingRecursiveComparison()
                .isEqualTo(expectedBusinessId);
    }

    @Test
    public void canBlockAddBusinessWithDuplicateName(long userId, AddBusinessDto businessDto, String mfaSecret) {
        given(businessRepository.existsByName(businessDto.name())).willReturn(true);

        assertThrows(RequestConflictException.class, () -> businessService.addBusiness(userId, businessDto));
    }

    @Test
    public void canUpdateBusiness(BusinessEntity expectedBusiness) {
        long businessId = expectedBusiness.getId();
        boolean autoAssigment = expectedBusiness.getAutoAssignment();
        UpdateBusinessDto businessDto = new UpdateBusinessDto(autoAssigment);

        given(businessRepository.findById(businessId)).willReturn(Optional.of(expectedBusiness.toBuilder()
                .autoAssignment(!autoAssigment)
                .build()));

        given(businessRepository.save(isA(BusinessEntity.class))).willReturn(expectedBusiness.toBuilder().build());

        businessService.updateBusiness(businessId, businessDto);

        verify(businessRepository).save(businessCaptor.capture());

        then(businessCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedBusiness);
    }

    @Test
    public void canNotUpdateNotFoundBusiness(long businessId, UpdateBusinessDto businessDto) {
        given(businessRepository.findById(businessId)).willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> businessService.updateBusiness(businessId, businessDto));
    }

    @Test
    public void canAddImageToBusiness(BusinessEntity expectedBusiness) {
        long businessId = expectedBusiness.getId();
        String logoPath = expectedBusiness.getLogoUrl();

        given(businessRepository.findById(businessId))
                .willReturn(Optional.of(expectedBusiness.toBuilder().build()));

        given(businessRepository.save(isA(BusinessEntity.class))).willReturn(expectedBusiness.toBuilder()
                .logoUrl(null)
                .build());

        businessService.addImageToBusiness(businessId, logoPath);

        verify(businessRepository).save(businessCaptor.capture());

        then(businessCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedBusiness);
    }

    @Test
    public void canNotAddImageToNotFoundBusiness(long businessId, String logoPath) {
        given(businessRepository.findById(businessId)).willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> businessService.addImageToBusiness(businessId, logoPath));
    }
}
