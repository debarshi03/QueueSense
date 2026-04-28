package com.example.QueueSense.QueueSense.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentRequestDto {
    private Long userId;
    private Long providerId;
    private LocalDateTime appointmentTime;
    private String reason;
}
