package com.temusik.filetools.services;

import com.temusik.filetools.models.Job;
import com.temusik.filetools.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobExecutionService {
    private final JobService jobService;
    private final JobRepository jobRepository;
    private final JobWorker jobWorker;

    @Transactional
    public Job startJob(UUID jobId) {
        //  Меняем статус всей задачи
        Job job = jobService.getJobOrThrow(jobId);
        if (!"PENDING".equals(job.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Job is not in PENDING state");
        }
        job.setStatus("RUNNING");
        job.setProgress(0);
        job.setStartedAt(Instant.now());
        jobRepository.save(job);
        // После смены статуса задачи, вызываем ее выполнение
        jobWorker.process(jobId);
        // После выполнения фронту вернем итог, пишу для себя, чтобы потом не запутаться, ПОТОМ УБЕРИ!!!
        return job;

    }
}
