package com.temusik.filetools.dto;

public record AddJobFileRequest (
        String role,
        String originalName,
        String contentType,
        Long sizeBytes,
        String storageKey
) {}
