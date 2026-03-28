package com.labourlink.dto;

import com.labourlink.entity.Job;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobResponse {
    private Long id;
    private Long contractorId;
    private String contractorName;
    private String contractorCompany;
    private String title;
    private String description;
    private String skillsRequired;
    private String location;
    private Double latitude;
    private Double longitude;
    private BigDecimal payRate;
    private String payType;
    private Job.Status status;
    private boolean urgent;
    private LocalDateTime createdAt;
    private int applicantCount;

    public static JobResponse from(Job job) {
        JobResponse r = new JobResponse();
        r.setId(job.getId());
        r.setContractorId(job.getContractor().getId());
        r.setContractorName(job.getContractor().getName());
        r.setTitle(job.getTitle());
        r.setDescription(job.getDescription());
        r.setSkillsRequired(job.getSkillsRequired());
        r.setLocation(job.getLocation());
        r.setLatitude(job.getLatitude());
        r.setLongitude(job.getLongitude());
        r.setPayRate(job.getPayRate());
        r.setPayType(job.getPayType());
        r.setStatus(job.getStatus());
        r.setUrgent(job.isUrgent());
        r.setCreatedAt(job.getCreatedAt());
        return r;
    }
}
