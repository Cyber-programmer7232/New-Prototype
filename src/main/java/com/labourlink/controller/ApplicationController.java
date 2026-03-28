package com.labourlink.controller;

import com.labourlink.dto.ApplicationResponse;
import com.labourlink.entity.Application;
import com.labourlink.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('CONTRACTOR')")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Application.Status status = Application.Status.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(applicationService.updateStatus(id, status));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('CONTRACTOR')")
    public ResponseEntity<ApplicationResponse> completeJob(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String photoUrl = body != null ? body.get("photoUrl") : null;
        String notes    = body != null ? body.get("notes")    : null;
        return ResponseEntity.ok(applicationService.completeJob(id, photoUrl, notes));
    }
}
