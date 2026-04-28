package com.example.QueueSense.QueueSense.dto;

import com.example.QueueSense.QueueSense.entity.type.AppointmentStatus;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusRequestDto {
    private Long appointmentId;
    private AppointmentStatus status;
}
