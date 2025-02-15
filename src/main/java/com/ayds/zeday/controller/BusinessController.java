package com.ayds.zeday.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ayds.zeday.config.annotation.CurrentUserDto;
import com.ayds.zeday.domain.dto.business.AddBusinessDto;
import com.ayds.zeday.domain.dto.business.BusinessDto;
import com.ayds.zeday.domain.dto.business.BusinessIdDto;
import com.ayds.zeday.domain.dto.business.BusinessLogoDto;
import com.ayds.zeday.domain.dto.business.UpdateBusinessDto;
import com.ayds.zeday.domain.dto.user.UserDto;
import com.ayds.zeday.domain.exception.ValueNotFoundException;
import com.ayds.zeday.service.business.BusinessService;
import com.ayds.zeday.service.util.FileStorageService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;
    private final FileStorageService fileStorageService;

    @GetMapping("/current")
    public ResponseEntity<BusinessDto> getCurrentBusiness(@RequestHeader("CompanyId") @Positive long businessId) {
        Optional<BusinessDto> business = businessService.findBusiness(businessId);

        return ResponseEntity.of(business);
    }

    @PostMapping
    public ResponseEntity<BusinessIdDto> createBusiness(@CurrentUserDto UserDto user,
            @RequestBody @Valid AddBusinessDto business) {
        long newBusinessId = businessService.addBusiness(user.getId(), business);

        return new ResponseEntity<>(new BusinessIdDto(newBusinessId), CREATED);
    }

    @PutMapping
    @ResponseStatus(NO_CONTENT)
    public void updateBusiness(@RequestHeader("CompanyId") @Positive long businessId,
            @RequestBody @Valid UpdateBusinessDto business) {
        businessService.updateBusiness(businessId, business);
    }

    @PatchMapping
    public ResponseEntity<BusinessLogoDto> updateBusinessLogo(@RequestHeader("CompanyId") @Positive long businessId,
            @RequestPart("logo") MultipartFile logo) {
        businessService.findBusiness(businessId)
                .orElseThrow(() -> new ValueNotFoundException("No se pudo encontrar la compañia"));

        String logoUrl = fileStorageService.store("business_" + businessId, logo);

        businessService.addImageToBusiness(businessId, logoUrl);

        return ResponseEntity.ok(new BusinessLogoDto(businessId, logoUrl));
    }
}
