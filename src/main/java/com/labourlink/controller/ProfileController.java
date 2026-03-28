package com.labourlink.controller;

import com.labourlink.dto.EndorsementRequest;
import com.labourlink.dto.RatingRequest;
import com.labourlink.entity.Endorsement;
import com.labourlink.entity.Rating;
import com.labourlink.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final RatingService ratingService;

    @GetMapping("/worker/profile/{id}")
    public ResponseEntity<Map<String, Object>> getWorkerProfile(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.getWorkerProfile(id));
    }

    @GetMapping("/contractor/profile/{id}")
    public ResponseEntity<Map<String, Object>> getContractorProfile(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.getContractorProfile(id));
    }

    @PostMapping("/applications/{id}/rate")
    public ResponseEntity<Rating> rate(
            @PathVariable Long id,
            @RequestBody RatingRequest request) {
        return ResponseEntity.ok(ratingService.submitRating(id, request));
    }

    @PostMapping("/applications/{id}/endorse")
    @PreAuthorize("hasRole('CONTRACTOR')")
    public ResponseEntity<Endorsement> endorse(
            @PathVariable Long id,
            @RequestBody EndorsementRequest request) {
        return ResponseEntity.ok(ratingService.addEndorsement(id, request));
    }
}
