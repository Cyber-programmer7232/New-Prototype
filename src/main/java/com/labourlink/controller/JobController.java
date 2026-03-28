package com.labourlink.controller;

import com.labourlink.dto.ApplicationResponse;
import com.labourlink.dto.JobRequest;
import com.labourlink.dto.JobResponse;
import com.labourlink.service.ApplicationService;
import com.labourlink.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final ApplicationService applicationService;

    @PostMapping("/jobs")
    @PreAuthorize("hasRole('CONTRACTOR')")
    public ResponseEntity<JobResponse> createJob(@RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.createJob(request));
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponse>> getJobs(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false, defaultValue = "50") Double radius) {
        return ResponseEntity.ok(jobService.getOpenJobs(skill, lat, lng, radius));
    }

    @GetMapping("/contractor/jobs")
    @PreAuthorize("hasRole('CONTRACTOR')")
    public ResponseEntity<List<JobResponse>> getContractorJobs() {
        return ResponseEntity.ok(jobService.getContractorJobs());
    }

    @PostMapping("/jobs/{jobId}/apply")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ApplicationResponse> apply(
            @PathVariable Long jobId,
            @RequestBody(required = false) Map<String, String> body) {
        String note = body != null ? body.get("coverNote") : null;
        return ResponseEntity.ok(applicationService.apply(jobId, note));
    }

    @GetMapping("/worker/applications")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<List<ApplicationResponse>> getWorkerApplications() {
        return ResponseEntity.ok(applicationService.getWorkerApplications());
    }

    @GetMapping("/contractor/jobs/{jobId}/applicants")
    @PreAuthorize("hasRole('CONTRACTOR')")
    public ResponseEntity<List<ApplicationResponse>> getApplicants(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getJobApplicants(jobId));
    }
}
