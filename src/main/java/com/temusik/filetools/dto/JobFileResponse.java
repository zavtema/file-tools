package com.temusik.filetools.dto;

import java.time.Instant;
import java.util.UUID;

public record JobFileResponse(
        UUID id,
        UUID jobId,
        String role,
        String originalName,
        String contentType,
        Long sizeBytes,
        String storageKey,
        Instant createdAt
) {}
