package com.temusik.filetools.dto;


import com.temusik.filetools.JobStatus.JobStatus;
import com.temusik.filetools.JobStatus.JobType;

import java.time.Instant;
import java.util.UUID;

public record JobResponse(
        UUID id,
        JobType type,
        JobStatus status,
        Integer progress,
        String optionsJson,
        String errorCode,
        String errorMessage,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt
) {}
