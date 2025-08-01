package com.dipal.NextCart.service;


import com.dipal.NextCart.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;

        File directory = new File(uploadDir);
        if (!directory.exists()) directory.mkdirs();

        File destination = new File(directory, uniqueFilename);
        try {
            file.transferTo(destination);
        } catch (IOException e) {
            throw new FileStorageException("Could not save file: " + originalFilename, e);
        }

        return "/" + uploadDir + "/" + uniqueFilename;
    }
}

