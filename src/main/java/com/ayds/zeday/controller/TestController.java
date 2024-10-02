package com.ayds.zeday.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.zeday.config.annotation.CurrentUserDto;
import com.ayds.zeday.domain.dto.user.UserDto;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    @GetMapping
    @PreAuthorize("hasAuthority('TESTS::READ')")
    public String getMethodName(@RequestParam String param, @CurrentUserDto UserDto user) {
        return user.getName();
    }
}
