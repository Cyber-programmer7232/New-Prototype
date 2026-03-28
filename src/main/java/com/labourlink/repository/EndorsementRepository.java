package com.labourlink.repository;

import com.labourlink.entity.Endorsement;
import com.labourlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EndorsementRepository extends JpaRepository<Endorsement, Long> {
    List<Endorsement> findByWorker(User worker);
}
