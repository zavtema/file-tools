package com.temusik.filetools.services;

import com.temusik.filetools.models.Job;
import com.temusik.filetools.models.JobFile;
import com.temusik.filetools.repository.JobFileRepository;
import com.temusik.filetools.repository.JobRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class JobService {

    private final JobFileRepository jobFileRepository;
    private final JobRepository jobRepository;

    public Job createJob(String type) {
        Job job = new Job();
        job.setType(type);
        job.setStatus("PENDING");
        return jobRepository.save(job);
    }

    public void addFiles(UUID jobId, List<JobFile> files) {
        Job job = jobRepository.findById(jobId) // Метод должен вернуть Optional<Job> opt = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found")); // Поэтому будет работать при подстраховке: "что делать если вернется не то"
        for (JobFile f: files) {
            f.setJob(job);
        }
        jobFileRepository.saveAll(files);
    }

    public List<JobFile> getFiles(UUID jobId) {
        return jobFileRepository.findAllByJob_Id(jobId);
    }
}
