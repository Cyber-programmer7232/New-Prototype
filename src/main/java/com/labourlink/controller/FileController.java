package com.labourlink.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "No file selected");
            return ResponseEntity.badRequest().body(err);
        }
        try {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            String filename = UUID.randomUUID() + "_" +
                    file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            Files.copy(file.getInputStream(), uploadPath.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);

            Map<String, String> result = new HashMap<>();
            result.put("url", "/uploads/" + filename);
            result.put("filename", filename);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Upload failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(err);
        }
    }
}
