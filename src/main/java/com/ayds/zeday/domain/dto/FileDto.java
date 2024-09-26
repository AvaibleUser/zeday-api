package com.ayds.zeday.domain.dto;

public record FileDto(
        byte[] content,
        String contentType) {
}
