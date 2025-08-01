package com.dipal.NextCart.config;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            System.out.println("✅ Upload directory created at: " + uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("❌ Could not create upload directory: " + uploadDir, e);
        }
    }
}
