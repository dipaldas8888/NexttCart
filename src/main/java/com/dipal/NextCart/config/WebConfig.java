package com.dipal.NextCart.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensure the path ends with a separator
        String path = Paths.get(uploadDir).toAbsolutePath().toString();
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + path);

    }
}

