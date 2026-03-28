package com.labourlink.service;

import com.labourlink.dto.ApplicationResponse;
import com.labourlink.entity.Application;
import com.labourlink.entity.Job;
import com.labourlink.entity.JobCompletion;
import com.labourlink.entity.User;
import com.labourlink.repository.ApplicationRepository;
import com.labourlink.repository.JobCompletionRepository;
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
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final JobCompletionRepository jobCompletionRepository;
    private final UserRepository userRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    @Transactional
    public ApplicationResponse apply(Long jobId, String coverNote) {
        User worker = getCurrentUser();
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        if (job.getStatus() != Job.Status.OPEN) {
            throw new IllegalStateException("Job is not open");
        }
        if (applicationRepository.existsByJobAndWorker(job, worker)) {
            throw new IllegalStateException("Already applied to this job");
        }
        Application app = applicationRepository.save(Application.builder()
                .job(job).worker(worker).coverNote(coverNote)
                .status(Application.Status.PENDING).build());
        return ApplicationResponse.from(app);
    }

    public List<ApplicationResponse> getWorkerApplications() {
        return applicationRepository.findByWorkerOrderByAppliedAtDesc(getCurrentUser())
                .stream().map(ApplicationResponse::from).collect(Collectors.toList());
    }

    public List<ApplicationResponse> getJobApplicants(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        return applicationRepository.findByJobOrderByAppliedAtDesc(job)
                .stream().map(ApplicationResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, Application.Status newStatus) {
        User contractor = getCurrentUser();
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if (!app.getJob().getContractor().getId().equals(contractor.getId())) {
            throw new SecurityException("Not authorized");
        }
        app.setStatus(newStatus);
        if (newStatus == Application.Status.ACCEPTED) {
            app.getJob().setStatus(Job.Status.IN_PROGRESS);
            jobRepository.save(app.getJob());
        }
        return ApplicationResponse.from(applicationRepository.save(app));
    }

    @Transactional
    public ApplicationResponse completeJob(Long applicationId, String photoUrl, String notes) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if (app.getStatus() != Application.Status.ACCEPTED) {
            throw new IllegalStateException("Application must be ACCEPTED first");
        }
        jobCompletionRepository.save(JobCompletion.builder()
                .application(app).photoUrl(photoUrl).notes(notes).build());
        app.setStatus(Application.Status.COMPLETED);
        app.getJob().setStatus(Job.Status.COMPLETED);
        jobRepository.save(app.getJob());
        return ApplicationResponse.from(applicationRepository.save(app));
    }

    public Application getById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
    }
}
