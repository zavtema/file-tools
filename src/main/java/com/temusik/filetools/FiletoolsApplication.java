package com.temusik.filetools;

import com.temusik.filetools.storage.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableAsync
public class FiletoolsApplication {
	public static void main(String[] args) {
		SpringApplication.run(FiletoolsApplication.class, args);
	}
}
