package com.labourlink.repository;

import com.labourlink.entity.ContractorProfile;
import com.labourlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ContractorProfileRepository extends JpaRepository<ContractorProfile, Long> {
    Optional<ContractorProfile> findByUser(User user);
    Optional<ContractorProfile> findByUserId(Long userId);
}
