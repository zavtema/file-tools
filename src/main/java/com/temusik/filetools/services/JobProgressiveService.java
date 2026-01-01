package com.temusik.filetools.services;

import com.temusik.filetools.JobStatus.JobStatus;
import com.temusik.filetools.models.Job;
import com.temusik.filetools.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobProgressiveService {
    private final JobRepository jobRepository;

    @Transactional
    public Job markRunning(UUID jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setStatus(JobStatus.RUNNING);
        job.setProgress(0);
        if (job.getStartedAt() == null) {
            job.setStartedAt(Instant.now());
        }
        job.setErrorCode(null);
        job.setErrorMessage(null);
        return jobRepository.save(job);
    }
    @Transactional
    public Job updateProgress(UUID jobId, int progress) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        int p = Math.max(0, Math.min(100, progress));
        job.setProgress(p);
        return jobRepository.save(job);
    }
    @Transactional
    public Job markDone(UUID jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setStatus(JobStatus.DONE);
        job.setProgress(100);
        job.setFinishedAt(Instant.now());
        return jobRepository.save(job);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Job markFailed(UUID jobId, String errorMessage) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        job.setStatus(JobStatus.FAILED);
        job.setFinishedAt(Instant.now());
        job.setErrorMessage(errorMessage);
        return jobRepository.save(job);
    }
}
