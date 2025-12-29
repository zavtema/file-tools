package com.temusik.filetools.dto;

public record StoredObject(
        String storageKey, // Внутренний идентификатор файла в storage
        String originalName, // Имя файла пришедшее от пользователя
        long size,
        String contentType) {}
