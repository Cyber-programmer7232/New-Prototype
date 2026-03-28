package com.labourlink.service;

import com.labourlink.dto.JobRequest;
import com.labourlink.dto.JobResponse;
import com.labourlink.entity.Job;
import com.labourlink.entity.User;
import com.labourlink.repository.ApplicationRepository;
import com.labourlink.repository.JobRepository;
import com.labourlink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    @Transactional
    public JobResponse createJob(JobRequest req) {
        User contractor = getCurrentUser();
        Job job = Job.builder()
                .contractor(contractor)
                .title(req.getTitle()).description(req.getDescription())
                .skillsRequired(req.getSkillsRequired())
                .location(req.getLocation()).latitude(req.getLatitude()).longitude(req.getLongitude())
                .payRate(req.getPayRate()).payType(req.getPayType())
                .urgent(req.isUrgent()).status(Job.Status.OPEN)
                .build();
        return toResponse(jobRepository.save(job));
    }

    public List<JobResponse> getOpenJobs(String skill, Double lat, Double lng, Double radius) {
        return jobRepository.findOpenJobsFiltered(skill, lat, lng, radius)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<JobResponse> getContractorJobs() {
        return jobRepository.findByContractorOrderByCreatedAtDesc(getCurrentUser())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + id));
    }

    private JobResponse toResponse(Job job) {
        JobResponse r = JobResponse.from(job);
        r.setApplicantCount(applicationRepository.findByJobOrderByAppliedAtDesc(job).size());
        return r;
    }
}
