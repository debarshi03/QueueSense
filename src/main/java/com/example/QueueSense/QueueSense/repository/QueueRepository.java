package com.example.QueueSense.QueueSense.repository;

import com.example.QueueSense.QueueSense.entity.Appointment;
import com.example.QueueSense.QueueSense.entity.QueueEntry;
import com.example.QueueSense.QueueSense.entity.type.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QueueRepository extends JpaRepository<QueueEntry, Long> {

    List<QueueEntry> findByStatusOrderByPositionAsc(QueueStatus status);
    Optional<QueueEntry> findByAppointment(Appointment appointment);


    List<QueueEntry> findByAppointment_Provider_IdOrderByPositionAsc(Long providerId);



    Optional<QueueEntry> findByAppointment_User_Id(Long userId);



    Optional<QueueEntry> findByAppointment_Id(Long appointmentId);

    Optional<QueueEntry> findByAppointment_User_IdAndStatusIn(
            Long userId,
            List<QueueStatus> statuses
    );

    Optional<QueueEntry> findByAppointment_Provider_IdAndStatus(Long providerId, QueueStatus status);


    List<QueueEntry> findByAppointment_Provider_IdAndStatusOrderByPositionAsc(
            Long providerId,
            QueueStatus status
    );



    int countByAppointment_Provider_Id(Long providerId);

    int countByAppointment_Provider_IdAndStatusIn(Long providerId, List<QueueStatus> statuses);



    Optional<QueueEntry> findFirstByAppointment_Provider_IdAndStatusOrderByPositionAsc(
            Long providerId,
            QueueStatus status
    );

    List<QueueEntry> findByAppointment_IdAndStatusIn(
            Long appointmentId,
            List<QueueStatus> statuses
    );




}
