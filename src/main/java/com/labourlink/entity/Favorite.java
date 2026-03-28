package com.labourlink.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorites", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"contractor_id", "worker_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contractor_id", nullable = false)
    private User contractor;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
