package com.issuepulse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * FileStorageService — Saves uploaded images to the local filesystem.
 *
 * Files are stored under the configured upload directory and served
 * as static resources via /uploads/** URL mapping.
 *
 * To switch to S3: replace the storeFile method with an AWS S3PutObject call
 * and return the S3 public URL instead of the local path.
 */
@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Stores a multipart file and returns its accessible URL path.
     *
     * @param file      the uploaded file
     * @param subFolder sub-directory, e.g. "complaints" or "resolutions"
     * @return relative URL path, e.g. "/uploads/complaints/abc123.jpg"
     */
    public String storeFile(MultipartFile file, String subFolder) {
        try {
            // Build target directory
            Path targetDir = Paths.get(uploadDir, subFolder);
            Files.createDirectories(targetDir);

            // Determine extension: prefer original filename, fall back to content-type, then .jpg
            String originalName = file.getOriginalFilename();
            String extension;
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            } else {
                String contentType = file.getContentType();
                if (contentType != null && contentType.contains("/")) {
                    String subtype = contentType.substring(contentType.indexOf("/") + 1);
                    extension = "." + subtype.replaceAll("[^a-zA-Z0-9]", "");
                } else {
                    extension = ".jpg";
                }
            }

            String uniqueName = UUID.randomUUID() + extension;
            Path targetPath = targetDir.resolve(uniqueName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + subFolder + "/" + uniqueName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    /** Deletes a previously stored file (best-effort; logs warning on failure). */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null) return;
        try {
            // Strip leading slash and convert URL to filesystem path
            String relativePath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
            Files.deleteIfExists(Paths.get(relativePath));
        } catch (IOException e) {
            System.err.println("Warning: Could not delete file: " + fileUrl);
        }
    }
}
