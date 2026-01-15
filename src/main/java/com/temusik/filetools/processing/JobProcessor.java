package com.temusik.filetools.processing;

import com.temusik.filetools.JobStatus.JobType;

import java.util.UUID;

public interface JobProcessor {
    JobType type(); // Какой job поддерживает
    void process(UUID jobId); // Обработка задачи по jobId
}
