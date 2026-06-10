package com.example.QueueSense.QueueSense.repository;

import com.example.QueueSense.QueueSense.entity.Appointment;
import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.entity.type.AppointmentStatus;
import com.example.QueueSense.QueueSense.entity.type.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByProviderId(Long id);
    List<Appointment> findByUserId(Long userId);

    long countByProvider_IdAndAppointmentTimeBetween(
            Long providerId,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByProvider_IdAndAppointmentTimeBetweenAndStatusNot(
            Long providerId,
            LocalDateTime start,
            LocalDateTime end,
            AppointmentStatus status
    );

    long countByProvider_Id(Long providerId);

    long countByProvider_IdAndStatus(
            Long providerId,
            AppointmentStatus status
    );

    Optional<Appointment> findByIdAndUser_Id(
            Long appointmentId,
            Long userId
    );
}