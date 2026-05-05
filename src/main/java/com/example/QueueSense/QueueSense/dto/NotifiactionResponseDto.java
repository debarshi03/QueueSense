package com.example.QueueSense.QueueSense.dto;

import lombok.Data;

@Data
public class NotifiactionResponseDto {
    private Long id;
    private String message;
    private UserDto user;
}
