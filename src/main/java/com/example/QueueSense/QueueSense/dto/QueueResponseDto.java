package com.example.QueueSense.QueueSense.dto;

import lombok.Data;

@Data
public class QueueResponseDto {

    private Long queueId;
    private Long appointmentId;

    private Integer position;
    private Integer estimatedWaitTime;

    private String status;
}