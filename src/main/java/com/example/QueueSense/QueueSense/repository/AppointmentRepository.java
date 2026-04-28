package com.example.QueueSense.QueueSense.repository;

import com.example.QueueSense.QueueSense.entity.Appointment;
import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.entity.type.AppointmentStatus;
import com.example.QueueSense.QueueSense.entity.type.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByProviderId(Long id);
    List<Appointment> findByUserId(Long userId);
}