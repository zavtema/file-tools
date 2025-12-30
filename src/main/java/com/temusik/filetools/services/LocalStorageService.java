package com.temusik.filetools.services;

import com.temusik.filetools.dto.StoredObject;
import com.temusik.filetools.storage.StorageProperties;
import com.temusik.filetools.storage.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

    private final StorageProperties storageProperties;

    private Path rootPath() {
        return Paths.get(storageProperties.getRoot()).toAbsolutePath().normalize();
    }

    // root — “диск”
    // storageKey — “адрес внутри диска”

    @Override
    public StoredObject save(UUID jobId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        UUID fileId = UUID.randomUUID();
        String storageKey = "jobs/" + jobId + "/input/" + fileId + ".pdf";

        Path root = rootPath();
        Path target = root.resolve(storageKey).normalize();
        if (!target.startsWith(root)) throw new IllegalArgumentException("Invalid storageKey");
        try {
            Files.createDirectories(target.getParent()); // Создание папки, getParent() берет на один уровень вверх и если target - это путь прямо до файла, то получается с getParent это путь на одну папку выше
            try (InputStream in = file.getInputStream()) { // Из файла считываем байты
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING); // И записываем их в файл по определенному пути
            }
        } catch (IOException e) {
                throw new RuntimeException("Failed to store file", e);
        }
        return new StoredObject(
                storageKey,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType()
        );
    }

    @Override
    public Resource loadAsResource(String storageKey) { // Возвращаем ссылку на файл
        Path root = rootPath();
        Path target = root.resolve(storageKey).normalize();

        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("Invalid storageKey");
        }

        if (!Files.exists(target) || !Files.isRegularFile(target)) {
            throw new RuntimeException("File not found: " + storageKey);
        }

        try {
            return new UrlResource(target.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to load file: " + storageKey, e);
        }
    }

    @Override
    public void delete(String storageKey) {
        Path root = rootPath();
        Path target = root.resolve(storageKey).normalize();
        if (!target.startsWith(root)) throw new IllegalArgumentException("Invalid storageKey");
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + storageKey, e);
        }
    }
}
