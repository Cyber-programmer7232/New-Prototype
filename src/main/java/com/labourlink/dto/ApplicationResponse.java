package com.labourlink.dto;

import com.labourlink.entity.Application;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String jobLocation;
    private Long workerId;
    private String workerName;
    private String workerEmail;
    private String workerPhone;
    private String workerSkills;
    private Application.Status status;
    private String coverNote;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

    public static ApplicationResponse from(Application app) {
        ApplicationResponse r = new ApplicationResponse();
        r.setId(app.getId());
        r.setJobId(app.getJob().getId());
        r.setJobTitle(app.getJob().getTitle());
        r.setJobLocation(app.getJob().getLocation());
        r.setWorkerId(app.getWorker().getId());
        r.setWorkerName(app.getWorker().getName());
        r.setWorkerEmail(app.getWorker().getEmail());
        r.setWorkerPhone(app.getWorker().getPhone());
        r.setStatus(app.getStatus());
        r.setCoverNote(app.getCoverNote());
        r.setAppliedAt(app.getAppliedAt());
        r.setUpdatedAt(app.getUpdatedAt());
        return r;
    }
}
