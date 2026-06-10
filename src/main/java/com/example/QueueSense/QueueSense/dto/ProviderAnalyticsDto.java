package com.example.QueueSense.QueueSense.dto;

import lombok.Data;

@Data
public class ProviderAnalyticsDto {

    private long totalAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private long noShowAppointments;
    private Integer avgWaitTime;

}
