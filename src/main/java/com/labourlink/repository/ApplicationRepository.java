package com.labourlink.repository;

import com.labourlink.entity.Application;
import com.labourlink.entity.Job;
import com.labourlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByWorkerOrderByAppliedAtDesc(User worker);
    List<Application> findByJobOrderByAppliedAtDesc(Job job);
    boolean existsByJobAndWorker(Job job, User worker);
    Optional<Application> findByJobAndWorker(Job job, User worker);
}
