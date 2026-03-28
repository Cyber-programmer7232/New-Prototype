package com.labourlink.repository;

import com.labourlink.entity.Rating;
import com.labourlink.entity.Application;
import com.labourlink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByApplicationAndFromUser(Application application, User fromUser);
    List<Rating> findByToUser(User toUser);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.toUser = :user")
    Double findAverageRatingForUser(@Param("user") User user);
}
