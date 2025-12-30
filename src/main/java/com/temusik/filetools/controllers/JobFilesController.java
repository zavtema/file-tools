package com.temusik.filetools.controllers;

import com.temusik.filetools.dto.JobFileResponse;
import com.temusik.filetools.mapper.JobFileMapper;
import com.temusik.filetools.services.JobFileService;
import com.temusik.filetools.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs/{jobId}/files")
public class JobFilesController {

    private final JobService jobService;
    private final JobFileService jobFileService;

    @GetMapping
    public List<JobFileResponse> list(@PathVariable UUID jobId) {
        return jobService.getFiles(jobId).stream()
                .map(x -> JobFileMapper.toResponse(x))
                .toList();
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Настройка значит, что "Этот endpoint принимает запросы только с типом Content-Type: multipart/form-data."
    public JobFileResponse saveFileToStorageAndBd(@PathVariable UUID jobId, @RequestParam("file") MultipartFile multipartFile) {
        var saved = jobFileService.saveFileToStorage(jobId,multipartFile);
        return JobFileMapper.toResponse(saved);
    }
}
