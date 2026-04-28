package com.example.QueueSense.QueueSense.entity;

import com.example.QueueSense.QueueSense.entity.type.ServiceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    private Integer averageServiceTime;

    private Boolean isAvailable;

    @OneToOne
    private User user;

    @OneToMany(mappedBy = "provider", cascade = {CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Appointment> appointments=new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
