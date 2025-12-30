package com.temusik.filetools;

import com.temusik.filetools.storage.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class FiletoolsApplication {
	public static void main(String[] args) {
		SpringApplication.run(FiletoolsApplication.class, args);
	}
}
