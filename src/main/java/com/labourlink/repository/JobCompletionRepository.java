package com.labourlink.repository;

import com.labourlink.entity.JobCompletion;
import com.labourlink.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JobCompletionRepository extends JpaRepository<JobCompletion, Long> {
    Optional<JobCompletion> findByApplication(Application application);
    boolean existsByApplication(Application application);
}
