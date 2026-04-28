package com.example.QueueSense.QueueSense.dto;

import com.example.QueueSense.QueueSense.entity.type.AppointmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentStatusResponseDto {
    private Long appointmentId;
    private AppointmentStatus status;
    private String message;
    private LocalDateTime updatedAt;
}
