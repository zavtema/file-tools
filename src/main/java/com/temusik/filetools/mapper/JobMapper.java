package com.temusik.filetools.mapper;

import com.temusik.filetools.dto.JobResponse;
import com.temusik.filetools.models.Job;

public final class JobMapper {
    private JobMapper() {} // Защита от "дурака", чтобы объект нельзя было создать от класса, в котором ключевой только один метод
    public static JobResponse toResponse(Job j) {
        if (j == null) return null;
        return new JobResponse(
                j.getId(),
                j.getType(),
                j.getStatus(),
                j.getProgress(),
                j.getOptionsJson(),
                j.getErrorCode(),
                j.getErrorMessage(),
                j.getCreatedAt(),
                j.getStartedAt(),
                j.getFinishedAt()
        );
    }
}
