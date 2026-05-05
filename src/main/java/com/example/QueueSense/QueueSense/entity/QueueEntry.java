package com.example.QueueSense.QueueSense.entity;

import com.example.QueueSense.QueueSense.entity.type.QueueStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Appointment appointment;

    private Integer position;

    private Integer estimatedWaitTime;


    private LocalDateTime expectedStartTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean notified;

    @Enumerated(EnumType.STRING)
    private QueueStatus status;

    public Boolean getNotified() {
        return notified;
    }
}
