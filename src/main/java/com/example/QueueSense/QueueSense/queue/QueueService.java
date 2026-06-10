package com.example.QueueSense.QueueSense.queue;

import com.example.QueueSense.QueueSense.dto.QueueResponseDto;
import com.example.QueueSense.QueueSense.dto.WaitTimeResponseDto;
import com.example.QueueSense.QueueSense.dto.WebSocketQueueDto;
import com.example.QueueSense.QueueSense.entity.Appointment;
import com.example.QueueSense.QueueSense.entity.QueueEntry;
import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.entity.type.QueueStatus;
import com.example.QueueSense.QueueSense.repository.QueueRepository;
import com.example.QueueSense.QueueSense.service.NotificationService;
import com.example.QueueSense.QueueSense.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;
    private final WebSocketService webSocketService;

    public QueueEntry addToQueue(Appointment appointment, int avgTime) {

        Long providerId = appointment.getProvider().getId();

        int activeCount = queueRepository
                .countByAppointment_Provider_IdAndStatusIn(
                        providerId,
                        List.of(QueueStatus.WAITING, QueueStatus.IN_PROGRESS)
                );

        int position = activeCount + 1;

        QueueStatus status = (position == 1)
                ? QueueStatus.IN_PROGRESS
                : QueueStatus.WAITING;

        QueueEntry entry = QueueEntry.builder()
                .appointment(appointment)
                .position(position)
                .estimatedWaitTime((position - 1) * avgTime)
                .status(status)
                .notified(false)
                .build();

        entry = queueRepository.save(entry);

        User user = appointment.getUser();

        notificationService.sendNotification(
                user,
                "You have been added to the queue. Position: " + position
        );

        if (status == QueueStatus.IN_PROGRESS) {
            notificationService.sendNotification(
                    user,
                    "It's your turn now"
            );
        }

        return entry;
    }

    public void recalculateQueue(Long providerId, int avgTime) {

        List<QueueEntry> list = queueRepository
                .findByAppointment_Provider_IdOrderByPositionAsc(providerId);

        int pos = 1;

        for (QueueEntry q : list) {

            if (q.getStatus() != QueueStatus.COMPLETED) {

                q.setPosition(pos);
                q.setEstimatedWaitTime((pos - 1) * avgTime);

                if (q.getStatus() == QueueStatus.WAITING &&
                        pos <= 2 &&
                        !Boolean.TRUE.equals(q.getNotified())) {

                    notificationService.sendNotification(
                            q.getAppointment().getUser(),
                            "Your turn is coming soon"
                    );

                    q.setNotified(true);
                }

                WebSocketQueueDto dto=WebSocketQueueDto.builder()
                        .appointmentId(q.getAppointment().getId())
                        .position(q.getPosition())
                        .estimatedWaitTime(q.getEstimatedWaitTime())
                        .status(q.getStatus().name())
                        .build();

                webSocketService.sendQueueUpdate(providerId,dto);
                pos++;
            }
        }

        queueRepository.saveAll(list);
    }


    public List<QueueResponseDto> getQueueByProvider(Long providerId) {

        return queueRepository
                .findByAppointment_Provider_IdOrderByPositionAsc(providerId)
                .stream()
                .map(entry -> {
                    QueueResponseDto dto = modelMapper.map(entry, QueueResponseDto.class);
                    dto.setAppointmentId(entry.getAppointment().getId());
                    dto.setStatus(entry.getStatus().name());
                    return dto;
                })
                .toList();
    }

    public List<QueueResponseDto> getUserQueue(Long userId) {

        QueueEntry entry = queueRepository
                .findByAppointment_User_IdAndStatusIn(
                        userId,
                        List.of(QueueStatus.WAITING, QueueStatus.IN_PROGRESS)
                )
                .orElseThrow(() -> new RuntimeException("No active queue found"));

        QueueResponseDto dto = modelMapper.map(entry, QueueResponseDto.class);
        dto.setAppointmentId(entry.getAppointment().getId());
        dto.setStatus(entry.getStatus().name());

        return List.of(dto);
    }

    public WaitTimeResponseDto getWaitTime(Long appointmentId, Long id) {
        QueueEntry entry=queueRepository.findByAppointment_IdAndStatusIn(
                appointmentId,
                List.of(QueueStatus.WAITING,QueueStatus.IN_PROGRESS)
        ).orElseThrow(() -> new RuntimeException("Queue entry not found"));

        if(!entry.getAppointment().getUser().getId().equals(id)){
            throw new RuntimeException("Unauthorized Acsess");
        }

        int avgTime=entry.getAppointment().getProvider().getAverageServiceTime();
        int waitTime=(entry.getPosition()-1)*avgTime;

        WaitTimeResponseDto responseDto= new WaitTimeResponseDto();
        responseDto.setWaitTime(waitTime);
        responseDto.setPosition(entry.getPosition());

        return responseDto;
    }
}
