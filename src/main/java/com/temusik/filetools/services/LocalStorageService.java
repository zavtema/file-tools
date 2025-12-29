package com.temusik.filetools.services;

import com.temusik.filetools.dto.StoredObject;
import com.temusik.filetools.storage.StorageProperties;
import com.temusik.filetools.storage.StorageService;

import lombok.RequiredArgsConstructor;


import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

    private final StorageProperties storageProperties;

    @Override
    public StoredObject save(UUID jobId, MultipartFile file) {
        UUID fileId = UUID.randomUUID();
        String storageKey = "jobs/" + jobId + "/input/" + fileId + ".pdf";
        return new StoredObject(
                storageKey,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
        );
    }

}

// В РАЗРАБОТКЕ, переделать первый и добавить еще 2 метода