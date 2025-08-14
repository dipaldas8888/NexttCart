package com.dipal.NextCart.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    private final Cloudinary cloudinary;

    public String saveFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String url = (String) uploadResult.get("secure_url");
            log.info("File uploaded to Cloudinary: {}", url);
            return url;  // Return the full URL of the uploaded image
        } catch (IOException e) {
            log.error("Could not upload file to Cloudinary: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Could not upload file: " + file.getOriginalFilename(), e);
        }
    }
}
