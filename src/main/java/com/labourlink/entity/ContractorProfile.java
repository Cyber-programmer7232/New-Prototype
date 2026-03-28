package com.labourlink.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "contractor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String companyName;
    private String location;
    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String description;
}
