package com.ayds.zeday.service.business;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayds.zeday.domain.dto.business.AddBusinessDto;
import com.ayds.zeday.domain.dto.business.BusinessDto;
import com.ayds.zeday.domain.dto.business.UpdateBusinessDto;
import com.ayds.zeday.domain.entity.BusinessEntity;
import com.ayds.zeday.domain.exception.RequestConflictException;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.repository.BusinessRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;

    private BusinessEntity findById(long businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new ValueNotFoundException("La compañia no se pudo encontrar"));
    }

    public Optional<BusinessDto> findBusiness(long businessId) {
        return businessRepository.findBusinessById(businessId);
    }

    @Transactional
    public long addBusiness(AddBusinessDto business) {
        if (businessRepository.existsByName(business.name())) {
            throw new RequestConflictException("El nombre de la compañia ya existe");
        }
        BusinessEntity newBusiness = new BusinessEntity(business.name(), business.autoAssignment());
        newBusiness = businessRepository.saveAndFlush(newBusiness);

        return newBusiness.getId();
    }

    @Transactional
    public void updateBusiness(long businessId, UpdateBusinessDto business) {
        BusinessEntity dbBusiness = findById(businessId);

        dbBusiness.setAutoAssignment(business.autoAssignment());

        businessRepository.save(dbBusiness);
    }

    @Transactional
    public void addImageToBusiness(long businessId, String logoUrl) {
        BusinessEntity dbBusiness = findById(businessId);

        dbBusiness.setLogoUrl(logoUrl);

        businessRepository.save(dbBusiness);
    }
}
