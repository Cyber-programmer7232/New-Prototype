package com.labourlink.repository;

import com.labourlink.entity.WorkerProfile;
import com.labourlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, Long> {
    Optional<WorkerProfile> findByUser(User user);
    Optional<WorkerProfile> findByUserId(Long userId);
}
