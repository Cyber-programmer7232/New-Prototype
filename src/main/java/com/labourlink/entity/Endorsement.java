package com.labourlink.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "endorsements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endorsement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    @Column(columnDefinition = "TEXT")
    private String skillDescription;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
