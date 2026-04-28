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


    // 🔥 1. Get full queue for a provider (ordered)
    List<QueueEntry> findByAppointment_Provider_IdOrderByPositionAsc(Long providerId);


    // 🔥 2. Get queue entry for a specific user
    Optional<QueueEntry> findByAppointment_User_Id(Long userId);


    // 🔥 3. Get queue entry by appointment (VERY IMPORTANT)
    Optional<QueueEntry> findByAppointment_Id(Long appointmentId);

    Optional<QueueEntry> findByAppointment_User_IdAndStatusIn(
            Long userId,
            List<QueueStatus> statuses
    );



    // 🔥 5. Get all queue entries for a provider by status
    List<QueueEntry> findByAppointment_Provider_IdAndStatusOrderByPositionAsc(
            Long providerId,
            QueueStatus status
    );


    // 🔥 6. Count queue size for a provider (better than global count)
    int countByAppointment_Provider_Id(Long providerId);


    // 🔥 7. Get next patient in queue
    Optional<QueueEntry> findFirstByAppointment_Provider_IdAndStatusOrderByPositionAsc(
            Long providerId,
            QueueStatus status
    );


}
