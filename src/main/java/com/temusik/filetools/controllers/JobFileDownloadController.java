package com.temusik.filetools.controllers;

import com.temusik.filetools.models.JobFile;
import com.temusik.filetools.repository.JobFileRepository;
import com.temusik.filetools.services.JobService;
import com.temusik.filetools.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class JobFileDownloadController {
    private final JobFileRepository jobFileRepository;
    private final StorageService storageService;

    @GetMapping("/api/files/{fileId}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID fileId) {
        JobFile file = jobFileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));
        Resource resource = storageService.loadAsResource(file.getStorageKey());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getOriginalName().replace("\"", "") + "\"")
                .contentType(MediaType.parseMediaType(
                        file.getContentType() != null ? file.getContentType() : "application/octet-stream"
                ))
                .body(resource);
    }
}
