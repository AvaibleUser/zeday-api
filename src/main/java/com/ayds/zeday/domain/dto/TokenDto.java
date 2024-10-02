package com.ayds.zeday.domain.dto;

import com.ayds.zeday.domain.dto.user.UserDto;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record TokenDto(
        String token,
        @JsonUnwrapped UserDto user) {
}
