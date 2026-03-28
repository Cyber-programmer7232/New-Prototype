package com.labourlink.service;

import com.labourlink.dto.EndorsementRequest;
import com.labourlink.dto.RatingRequest;
import com.labourlink.entity.*;
import com.labourlink.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final EndorsementRepository endorsementRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final ContractorProfileRepository contractorProfileRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    @Transactional
    public Rating submitRating(Long applicationId, RatingRequest req) {
        User from = getCurrentUser();
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if (app.getStatus() != Application.Status.COMPLETED) {
            throw new IllegalStateException("Job must be completed before rating");
        }
        if (ratingRepository.existsByApplicationAndFromUser(app, from)) {
            throw new IllegalStateException("Already rated");
        }
        if (req.getRating() < 1 || req.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be 1-5");
        }
        User to = from.getId().equals(app.getJob().getContractor().getId())
                ? app.getWorker() : app.getJob().getContractor();

        return ratingRepository.save(Rating.builder()
                .fromUser(from).toUser(to).application(app)
                .rating(req.getRating()).comment(req.getComment()).build());
    }

    @Transactional
    public Endorsement addEndorsement(Long applicationId, EndorsementRequest req) {
        User contractor = getCurrentUser();
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if (!app.getJob().getContractor().getId().equals(contractor.getId())) {
            throw new SecurityException("Only the contractor can endorse");
        }
        if (app.getStatus() != Application.Status.COMPLETED) {
            throw new IllegalStateException("Job must be completed before endorsing");
        }
        return endorsementRepository.save(Endorsement.builder()
                .application(app).worker(app.getWorker())
                .skillDescription(req.getSkillDescription()).build());
    }

    public Map<String, Object> getWorkerProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        WorkerProfile profile = workerProfileRepository.findByUserId(userId).orElse(null);
        List<Rating> ratings = ratingRepository.findByToUser(user);
        List<Endorsement> endorsements = endorsementRepository.findByWorker(user);
        Double avg = ratingRepository.findAverageRatingForUser(user);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("name", user.getName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("profile", profile);
        result.put("ratings", ratings);
        result.put("endorsements", endorsements);
        result.put("averageRating", avg != null ? avg : 0.0);
        return result;
    }

    public Map<String, Object> getContractorProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        ContractorProfile profile = contractorProfileRepository.findByUserId(userId).orElse(null);
        List<Rating> ratings = ratingRepository.findByToUser(user);
        Double avg = ratingRepository.findAverageRatingForUser(user);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("name", user.getName());
        result.put("email", user.getEmail());
        result.put("profile", profile);
        result.put("ratings", ratings);
        result.put("averageRating", avg != null ? avg : 0.0);
        return result;
    }
}
