package com.example.QueueSense.QueueSense.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketNotificationDto {

    private Long userId;

    private String message;

}