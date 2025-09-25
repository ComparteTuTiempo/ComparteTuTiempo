package com.compartetutiempo.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    private final Path uploadDir;

    public StorageService(@Value("${file.upload-dir}") String uploadDir) throws IOException {
        this.uploadDir = Paths.get(uploadDir);
        if (!Files.exists(this.uploadDir)) {
            Files.createDirectories(this.uploadDir);
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(filename);
        Files.write(filePath, file.getBytes());
        return "/uploads/" + filename; // URL relativa
    }
}
