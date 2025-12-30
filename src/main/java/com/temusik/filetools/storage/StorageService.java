package com.temusik.filetools.storage;

import com.temusik.filetools.dto.StoredObject;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StorageService {
    void delete(String storageKey);
    StoredObject save (UUID jobId, MultipartFile file);
    Resource loadAsResource(String storageKey); // Объяснения Resourse см. в папке инфо, если кратко: "Resource — это “ручка” (handle) к файлу, а не сам файл."
}
