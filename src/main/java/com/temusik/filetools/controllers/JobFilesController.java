package com.temusik.filetools.controllers;

import com.temusik.filetools.dto.AddJobFileRequest;
import com.temusik.filetools.dto.JobFileResponse;
import com.temusik.filetools.mapper.JobFileMapper;
import com.temusik.filetools.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs/{jobId}/files")
public class JobFilesController {

    private final JobService jobService;

    @GetMapping
    public List<JobFileResponse> list(@PathVariable UUID jobId) {
        return jobService.getFiles(jobId).stream()
                .map(x -> JobFileMapper.toResponse(x))
                .toList();
    }
    @PostMapping
    public JobFileResponse add(@PathVariable UUID jobId,
                               @RequestBody AddJobFileRequest req) {
        var file = jobService.addFile(
                jobId,
                req.role(),
                req.originalName(),
                req.contentType(),
                req.sizeBytes(),
                req.storageKey()
        );
        return JobFileMapper.toResponse(file);
    }
}
