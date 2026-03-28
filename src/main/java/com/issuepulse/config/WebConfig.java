package com.issuepulse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * WebConfig — Serves uploaded images as static resources.
 *
 * Any file saved under the "uploads/" folder is accessible via:
 *   http://localhost:8080/uploads/complaints/abc.jpg
 *   http://localhost:8080/uploads/resolutions/xyz.jpg
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /uploads/** URL to the local uploads/ directory
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + uploadDir + "/");
    }
}
