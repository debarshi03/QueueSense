package com.example.QueueSense.QueueSense.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketQueueDto {

    private Long appointmentId;

    private Integer position;

    private Integer estimatedWaitTime;

    private String status;
}
