package com.ayds.zeday.service.business;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ayds.zeday.config.annotation.ZedayTest;
import com.ayds.zeday.domain.dto.business.AddBusinessDto;
import com.ayds.zeday.domain.dto.business.BusinessDto;
import com.ayds.zeday.domain.dto.business.UpdateBusinessDto;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.BusinessRepository;
import com.ayds.zeday.util.RandomUtils;

@ZedayTest
public class BusinessServiceTest {

    private static final RandomUtils random = new RandomUtils();

    @MockBean
    private BusinessRepository businessRepository;

    @Autowired
    private BusinessService businessService;

    @Test
    public void canFindBusiness() {
        long businessId = random.nextPositiveLong();
        BusinessDto expectedBusiness = BusinessDto.builder()
                .id(businessId)
                .name(random.nextString())
                .autoAssignment(random.nextBoolean())
                .logoUrl(random.nextString())
                .createdAt(random.nextInstant())
                .updatedAt(random.nextInstant())
                .build();

        given(businessRepository.findBusinessById(businessId))
                .willReturn(Optional.of(expectedBusiness.toBuilder().build()));

        Optional<BusinessDto> actualBusiness = businessService.findBusiness(businessId);

        then(actualBusiness).contains(expectedBusiness);
    }

    @Test
    public void canFindBusinessAndNotFoundIt() {
        long businessId = random.nextPositiveLong();

        given(businessRepository.findBusinessById(businessId)).willReturn(Optional.empty());

        Optional<BusinessDto> actualBusiness = businessService.findBusiness(businessId);

        then(actualBusiness).isEmpty();
    }

    @Test
    public void canAddBusiness() {
        long expectedBusinessId = random.nextPositiveLong();
        BusinessEntity expectedBusiness = BusinessEntity.builder()
                .id(expectedBusinessId)
                .name(random.nextString())
                .autoAssignment(random.nextBoolean())
                .build();

        AddBusinessDto businessDto = AddBusinessDto.builder()
                .name(expectedBusiness.getName())
                .autoAssignment(expectedBusiness.getAutoAssignment())
                .build();

        BusinessEntity business = expectedBusiness.toBuilder()
                .id(null)
                .build();

        given(businessRepository.existsByName(businessDto.name())).willReturn(false);
        given(businessRepository.saveAndFlush(business))
                .willReturn(expectedBusiness.toBuilder().build());

        long actualBusinessId = businessService.addBusiness(businessDto);

        then(actualBusinessId).isEqualTo(expectedBusinessId);
    }

    @Test
    public void canBlockAddBusinessWithDuplicateName() {
        AddBusinessDto businessDto = AddBusinessDto.builder()
                .name(random.nextString())
                .autoAssignment(random.nextBoolean())
                .build();

        given(businessRepository.existsByName(businessDto.name())).willReturn(true);

        assertThrows(RequestConflictException.class, () -> businessService.addBusiness(businessDto));
    }

    @Test
    public void canUpdateBusiness() {
        long businessId = random.nextPositiveLong();
        boolean autoAssigment = random.nextBoolean();
        UpdateBusinessDto businessDto = new UpdateBusinessDto(autoAssigment);
        BusinessEntity expectedBusiness = BusinessEntity.builder()
                .id(businessId)
                .name(random.nextString())
                .autoAssignment(!autoAssigment)
                .build();

        given(businessRepository.findById(isA(Long.class))).willReturn(Optional.of(expectedBusiness));
        given(businessRepository.save(isA(BusinessEntity.class))).willReturn(expectedBusiness.toBuilder()
                .autoAssignment(autoAssigment)
                .build());

        businessService.updateBusiness(businessId, businessDto);

        verify(businessRepository).findById(businessId);
        verify(businessRepository).save(expectedBusiness);
    }

    @Test
    public void canNotUpdateNotFoundBusiness() {
        long businessId = random.nextPositiveLong();
        UpdateBusinessDto businessDto = new UpdateBusinessDto(random.nextBoolean());

        given(businessRepository.findById(businessId)).willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> businessService.updateBusiness(businessId, businessDto));
    }

    @Test
    public void canAddImageToBusiness() {
        long businessId = random.nextPositiveLong();
        String logoPath = random.nextString();
        BusinessEntity expectedBusiness = BusinessEntity.builder()
                .id(businessId)
                .name(random.nextString())
                .autoAssignment(random.nextBoolean())
                .logoUrl(logoPath)
                .build();

        given(businessRepository.findById(isA(Long.class))).willReturn(Optional.of(expectedBusiness));
        given(businessRepository.save(isA(BusinessEntity.class))).willReturn(expectedBusiness.toBuilder()
                .logoUrl(null)
                .build());

        businessService.addImageToBusiness(businessId, logoPath);

        verify(businessRepository).findById(businessId);
        verify(businessRepository).save(expectedBusiness);
    }

    @Test
    public void canNotAddImageToNotFoundBusiness() {
        long businessId = random.nextPositiveLong();
        String logoPath = random.nextString();

        given(businessRepository.findById(businessId)).willReturn(Optional.empty());

        assertThrows(ValueNotFoundException.class, () -> businessService.addImageToBusiness(businessId, logoPath));
    }
}
