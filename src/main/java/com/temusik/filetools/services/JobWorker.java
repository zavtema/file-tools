package com.temusik.filetools.services;

import com.temusik.filetools.JobStatus.JobStatus;
import com.temusik.filetools.models.Job;
import com.temusik.filetools.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobWorker {
    private final JobRepository jobRepository;

    @Async
    public void process(UUID jobId) {
        try {

            Thread.sleep(3000); // Это надо завтра заменить на сам рабочий метод, все, что ниже просто меняет структуру Job на как бы "СДЕЛАНО"

            Job job = jobRepository.findById(jobId).orElseThrow();
            job.setProgress(100);
            job.setStatus(JobStatus.DONE);
            job.setFinishedAt(Instant.now());
            jobRepository.save(job);

        } catch (Exception e) {
            Job job = jobRepository.findById(jobId).orElse(null);
            if (job != null) {
                job.setStatus(JobStatus.FAILED);
                job.setFinishedAt(Instant.now());
                job.setErrorMessage(e.getMessage());
                jobRepository.save(job);
            }
        }
    }
}
