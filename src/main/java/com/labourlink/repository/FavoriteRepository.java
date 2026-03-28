package com.labourlink.repository;

import com.labourlink.entity.Favorite;
import com.labourlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByContractor(User contractor);
    Optional<Favorite> findByContractorAndWorker(User contractor, User worker);
    boolean existsByContractorAndWorker(User contractor, User worker);
}
