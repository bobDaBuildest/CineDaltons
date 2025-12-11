package com.cinedaltons.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

// LicenseController.java
@RestController
public class LicenseController {

    @GetMapping("/license-text")
    public ResponseEntity<String> getLicenseText() {
        try {
            // Ανάγνωση του αρχείου LICENSE.TXT
            Resource resource = new ClassPathResource("license/LICENSE.TXT");
            String licenseText = new String(Files.readAllBytes(resource.getFile().toPath()));
            return ResponseEntity.ok(licenseText);
        } catch (IOException e) {
            // Εναλλακτικά, επιστροφή ενσωματωμένου κειμένου
            String defaultLicense = "MIT License\n\nCopyright (c) 2024 CineDaltons\n\nPermission is hereby granted...";
            return ResponseEntity.ok(defaultLicense);
        }
    }
}

/*
@RestController
public class LicenseController {

    @GetMapping("/license-text")
    public String getLicense() throws Exception {
        ClassPathResource resource =
                new ClassPathResource("static/license/LICENSE.txt");

        return StreamUtils.copyToString(
                resource.getInputStream(),
                StandardCharsets.UTF_8
        );
    }
}*/
