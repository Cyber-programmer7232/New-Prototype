package com.labourlink.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class JobRequest {
    private String title;
    private String description;
    private String skillsRequired;
    private String location;
    private Double latitude;
    private Double longitude;
    private BigDecimal payRate;
    private String payType;
    private boolean urgent;
}
