package com.example.QueueSense.QueueSense.dto;

import lombok.Data;

@Data
public class OnboardServiceProviderRequestDto {
    private Long userId;
    private String name;
    private String email;
    private Integer averageServiceTime;
    private Integer maxAppointment;

}
