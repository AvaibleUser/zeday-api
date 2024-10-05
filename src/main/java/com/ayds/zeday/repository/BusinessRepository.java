package com.ayds.zeday.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayds.zeday.domain.dto.business.BusinessDto;
import com.ayds.zeday.domain.entity.BusinessEntity;

@Repository
public interface BusinessRepository extends JpaRepository<BusinessEntity, Long> {

    Optional<BusinessDto> findBusinessById(Long businessId);

    boolean existsByName(String businessName);
}
