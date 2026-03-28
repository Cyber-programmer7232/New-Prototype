package com.labourlink.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "worker_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String skills;

    private String location;
    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String profilePhoto;
}
