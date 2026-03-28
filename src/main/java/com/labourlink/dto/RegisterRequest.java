package com.labourlink.dto;

import com.labourlink.entity.User;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private User.Role role;

    // Worker fields
    private String skills;
    private String bio;

    // Contractor fields
    private String companyName;

    // Shared
    private String location;
    private Double latitude;
    private Double longitude;
}
