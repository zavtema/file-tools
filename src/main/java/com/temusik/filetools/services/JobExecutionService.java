package com.temusik.filetools.services;

import com.temusik.filetools.JobStatus.JobStatus;
import com.temusik.filetools.models.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobExecutionService {
    private final JobService jobService;
    private final JobProgressiveService progressService;
    private final JobWorker jobWorker;

    public Job startJob(UUID jobId) {
        Job job = jobService.getJobOrThrow(jobId);

        if (job.getStatus() != JobStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Job is not in PENDING state");
        }
        progressService.markRunning(jobId);   // надо запустить через отдельный сервис
        jobWorker.process(jobId);             // запускаем async

        return jobService.getJobOrThrow(jobId); // вернем RUNNING
    }
}
