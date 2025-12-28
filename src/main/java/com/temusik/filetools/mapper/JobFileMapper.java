package com.temusik.filetools.mapper;

import com.temusik.filetools.dto.JobFileResponse;
import com.temusik.filetools.models.JobFile;

public final class JobFileMapper {
    private JobFileMapper() {} // Защита от "дурака", чтобы объект нельзя было создать от класса, в котором ключевой только один метод
    public static JobFileResponse toResponse(JobFile f) {
        if (f == null) return null;
        return new JobFileResponse(
                f.getId(),
                f.getJob() != null ? f.getJob().getId() : null,
                f.getRole(),
                f.getOriginalName(),
                f.getContentType(),
                f.getSizeBytes(),
                f.getStorageKey(),
                f.getCreatedAt()
        );
    }
}
