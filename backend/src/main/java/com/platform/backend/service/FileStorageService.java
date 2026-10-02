package com.platform.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {
    private final Path masterStoragePath = Paths.get("storage", "master").toAbsolutePath().normalize();
    private final Path watermarkedStoragePath = Paths.get("storage", "watermarked").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            Files.createDirectories(masterStoragePath);
            Files.createDirectories(watermarkedStoragePath);
        } catch (IOException e) {
            log.error("Could not initialize storage directories", e);
        }
    }

    public String storeMasterFile(MultipartFile file, UUID productId) throws IOException {
        String filename = productId.toString() + "_" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
        Path targetLocation = this.masterStoragePath.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        log.info("Saved master PDF to {}", targetLocation);
        return targetLocation.toString();
    }

    public Resource loadFileAsResource(String filePathStr) {
        try {
            Path filePath = Paths.get(filePathStr).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new IllegalArgumentException("File not found or unreadable: " + filePathStr);
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("File path is invalid: " + filePathStr, e);
        }
    }
}