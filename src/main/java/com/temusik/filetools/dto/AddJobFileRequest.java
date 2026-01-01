package com.temusik.filetools.dto;

import com.temusik.filetools.JobStatus.JobFileRole;

public record AddJobFileRequest (
        JobFileRole role,
        String originalName,
        String contentType,
        Long sizeBytes,
        String storageKey
) {}
