package com.temusik.filetools.dto;

import com.temusik.filetools.JobStatus.JobFileRole;

import java.time.Instant;
import java.util.UUID;

public record JobFileResponse(
        UUID id,
        UUID jobId,
        JobFileRole role,
        String originalName,
        String contentType,
        Long sizeBytes,
        String storageKey,
        Instant createdAt
) {}
