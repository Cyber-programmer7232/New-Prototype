package com.labourlink.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contractor_id", nullable = false)
    private User contractor;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String skillsRequired;

    private String location;
    private Double latitude;
    private Double longitude;

    private BigDecimal payRate;
    private String payType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.OPEN;

    @Column(name = "is_urgent")
    @Builder.Default
    private boolean urgent = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum Status {
        OPEN, IN_PROGRESS, COMPLETED
    }
}
