package com.labourlink.repository;

import com.labourlink.entity.Job;
import com.labourlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByContractorOrderByCreatedAtDesc(User contractor);

    @Query(value =
        "SELECT * FROM jobs j " +
        "WHERE j.status = 'OPEN' " +
        "AND (:skill IS NULL OR j.skills_required LIKE CONCAT('%',:skill,'%')) " +
        "AND ( " +
        "    :lat IS NULL OR :lng IS NULL OR :radius IS NULL OR " +
        "    (6371 * acos(" +
        "        cos(radians(:lat)) * cos(radians(j.latitude))" +
        "        * cos(radians(j.longitude) - radians(:lng))" +
        "        + sin(radians(:lat)) * sin(radians(j.latitude))" +
        "    )) <= :radius " +
        ") " +
        "ORDER BY j.is_urgent DESC, j.created_at DESC",
        nativeQuery = true)
    List<Job> findOpenJobsFiltered(
            @Param("skill") String skill,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Double radius
    );
}
