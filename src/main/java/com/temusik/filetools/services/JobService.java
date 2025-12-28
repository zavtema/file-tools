package com.temusik.filetools.services;

import com.temusik.filetools.models.Job;
import com.temusik.filetools.models.JobFile;
import com.temusik.filetools.repository.JobFileRepository;
import com.temusik.filetools.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class JobService {

    private final JobFileRepository jobFileRepository;
    private final JobRepository jobRepository;

    public Job createJob(String type, String optionsJson) {
        Job job = new Job();
        job.setType(type);
        job.setStatus("PENDING");
        job.setProgress(0);
        job.setOptionsJson(optionsJson);
        return jobRepository.save(job); // save возвращает из бд уже сохраненную сущность
    }

    public JobFile addFile(UUID jobId,
                           String role,
                           String originalName,
                           String contentType,
                           Long sizeBytes,
                           String storageKey) {
        Job job = getJobOrThrow(jobId);
        JobFile f = new JobFile();
        f.setJob(job);
        f.setRole(role);
        f.setOriginalName(originalName);
        f.setContentType(contentType);
        f.setSizeBytes(sizeBytes);
        f.setStorageKey(storageKey);
        return jobFileRepository.save(f);
    }

    public List<JobFile> getFiles(UUID jobId) {
        getJobOrThrow(jobId);
        return jobFileRepository.findAllByJob_Id(jobId);
    }

    public Job getJobOrThrow(UUID jobId) {
        return jobRepository.findById(jobId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
    }
}
