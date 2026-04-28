package com.example.QueueSense.QueueSense.dto;

import lombok.Data;

import java.util.List;

@Data
public class AppointmentResponseDto {
    private Long id;
    private String reason;
    private ServiceProviderResponseDto provider;
    private UserDto user;

}
