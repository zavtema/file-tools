package com.temusik.filetools.controllers;

import com.temusik.filetools.JobStatus.JobType;
import com.temusik.filetools.dto.CreateJobRequest;
import com.temusik.filetools.dto.JobResponse;
import com.temusik.filetools.mapper.JobMapper;
import com.temusik.filetools.models.Job;
import com.temusik.filetools.services.JobExecutionService;
import com.temusik.filetools.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final JobExecutionService jobExecutionService;

    // Создание Job
    @PostMapping()
    public JobResponse createJob(@RequestBody CreateJobRequest req) { // Spring будет парсить ровно по ключам, имена ключей ДОЛЖНЫ СОВПАДАТЬ
        var job = jobService.createJob(JobType.valueOf(req.type()), req.optionsJson());
        return JobMapper.toResponse(job);
    }

    @GetMapping("/{jobId}") // Получение Job
    public JobResponse getJob(@PathVariable UUID jobId) {
        var job = jobService.getJobOrThrow(jobId);
        return JobMapper.toResponse(job);
    }

    @PostMapping("/{jobId}/start")
    public JobResponse start(@PathVariable UUID jobId) {
        Job job = jobExecutionService.startJob(jobId);
        return JobMapper.toResponse(job);
    }
}
