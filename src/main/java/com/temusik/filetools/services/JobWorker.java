package com.temusik.filetools.services;

import com.temusik.filetools.models.Job;
import com.temusik.filetools.processing.JobProcessor;
import com.temusik.filetools.processing.JobProcessorRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobWorker {

    private final JobService jobService;
    private final JobProgressiveService progressService;
    private final JobProcessorRegistry registry;

    @Async
    public void process(UUID jobId) {
        try {
            Job job = jobService.getJobOrThrow(jobId);

            JobProcessor processor = registry.get(job.getType());
            processor.process(jobId);

            progressService.markDone(jobId);

        } catch (Exception e) {
            progressService.markFailed(jobId, e.getMessage());
        }
    }
}
