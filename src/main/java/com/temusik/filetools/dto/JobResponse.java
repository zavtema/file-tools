package com.temusik.filetools.dto;


import java.time.Instant;
import java.util.UUID;

public record JobResponse(
        UUID id,
        String type,
        String status,
        Integer progress,
        String optionsJson,
        String errorCode,
        String errorMessage,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt
) {}
