package com.temusik.filetools.services;

import com.temusik.filetools.dto.StoredObject;
import com.temusik.filetools.models.JobFile;
import com.temusik.filetools.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobFileService {

    private final StorageService localStorageService;
    private final JobService jobService;

    public JobFile saveFileToStorage(UUID jobId, MultipartFile multipartFile) {
        StoredObject storedObject = localStorageService.save(jobId, multipartFile);
        return jobService.addFile(jobId, "INPUT",
                storedObject.originalName(),
                storedObject.contentType(),
                storedObject.size(),
                storedObject.storageKey()
        );
    }
}
