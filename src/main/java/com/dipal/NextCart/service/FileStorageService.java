package com.dipal.NextCart.service;


import com.dipal.NextCart.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {
        try {
            // Create the uploads directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir,"images");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;

            Path filePath = uploadPath.resolve(uniqueFilename);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("File saved successfully: {}", filePath);
            return "/uploads/images/" + uniqueFilename;

        } catch (IOException ex) {
            log.error("Could not save file: {}", file.getOriginalFilename(), ex);
            throw new FileStorageException("Could not save file: " + file.getOriginalFilename(), ex);
        }
    }
}
