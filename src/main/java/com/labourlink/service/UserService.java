package com.labourlink.service;

import com.labourlink.dto.AuthResponse;
import com.labourlink.dto.LoginRequest;
import com.labourlink.dto.RegisterRequest;
import com.labourlink.entity.ContractorProfile;
import com.labourlink.entity.User;
import com.labourlink.entity.WorkerProfile;
import com.labourlink.repository.ContractorProfileRepository;
import com.labourlink.repository.UserRepository;
import com.labourlink.repository.WorkerProfileRepository;
import com.labourlink.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final ContractorProfileRepository contractorProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .role(req.getRole())
                .build();
        user = userRepository.save(user);

        if (req.getRole() == User.Role.WORKER) {
            workerProfileRepository.save(WorkerProfile.builder()
                    .user(user).skills(req.getSkills()).bio(req.getBio())
                    .location(req.getLocation()).latitude(req.getLatitude())
                    .longitude(req.getLongitude()).build());
        } else if (req.getRole() == User.Role.CONTRACTOR) {
            contractorProfileRepository.save(ContractorProfile.builder()
                    .user(user).companyName(req.getCompanyName())
                    .location(req.getLocation()).latitude(req.getLatitude())
                    .longitude(req.getLongitude()).build());
        }
        return new AuthResponse(jwtUtil.generateToken(user.getEmail()),
                user.getRole().name(), user.getId(), user.getName(), user.getEmail());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return new AuthResponse(jwtUtil.generateToken(user.getEmail()),
                user.getRole().name(), user.getId(), user.getName(), user.getEmail());
    }
}
