package com.temusik.filetools.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage") // Тот самый "доставатель" конкретного пути
public class StorageProperties {
    private String root = "storage"; // значение по умолчанию

    public String getRoot() { return root;}
    public void setRoot(String root) { this.root = root;}
}