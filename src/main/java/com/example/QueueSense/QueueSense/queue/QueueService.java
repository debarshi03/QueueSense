package com.example.QueueSense.QueueSense.queue;

import com.example.QueueSense.QueueSense.dto.QueueResponseDto;
import com.example.QueueSense.QueueSense.entity.Appointment;
import com.example.QueueSense.QueueSense.entity.QueueEntry;
import com.example.QueueSense.QueueSense.entity.ServiceProvider;
import com.example.QueueSense.QueueSense.entity.type.QueueStatus;
import com.example.QueueSense.QueueSense.repository.AppointmentRepository;
import com.example.QueueSense.QueueSense.repository.QueueRepository;
import com.example.QueueSense.QueueSense.repository.ServiceProviderRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final QueueRepository queueRepository;
    private final AppointmentRepository appointmentRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ModelMapper modelMapper;

    public QueueEntry addToQueue(Appointment appointment, int avgTime){
        int position = queueRepository.countByAppointment_Provider_Id(appointment.getProvider().getId()) + 1;
        QueueEntry queueEntry=QueueEntry.builder()
                .appointment(appointment)
                .position(position)
                .estimatedWaitTime(position*avgTime)
                .status(QueueStatus.WAITING)
                .build();

        return queueEntry;
    }

    public void recalculateQueue(int avgTime) {

        List<QueueEntry> list = queueRepository.findByStatusOrderByPositionAsc(QueueStatus.WAITING);

        int pos = 1;

        for (QueueEntry q : list) {
            q.setPosition(pos);
            q.setEstimatedWaitTime(pos * avgTime);
            pos++;
        }

        queueRepository.saveAll(list);
    }

    public List<QueueResponseDto> getQueueByProvider(Long id) {
        return queueRepository.findByAppointment_Provider_IdOrderByPositionAsc(id)
                .stream()
                .map(queueEntry ->{
                    QueueResponseDto dto=modelMapper.map(queueEntry, QueueResponseDto.class);
                    dto.setAppointmentId(queueEntry.getAppointment().getId());
                    dto.setStatus(queueEntry.getStatus().name());
                    return dto;
                        })
                .toList();
    }

//    public @Nullable List<QueueResponseDto> getUserQueue(Long id) {
//        return queueRepository.findByAppointment_User_Id(id)
//                .stream()
//                .map(queueEntry -> {
//                    QueueResponseDto dto=modelMapper.map(queueEntry, QueueResponseDto.class);
//                    dto.setAppointmentId(queueEntry.getAppointment().getId());
//                    dto.setStatus((queueEntry.getStatus().name()));
//                    return dto;
//                        })
//                .toList();
//    }

    public List<QueueResponseDto> getUserQueue(Long id) {

        QueueEntry entry = queueRepository
                .findByAppointment_User_IdAndStatusIn(
                     id,
                        List.of(QueueStatus.WAITING, QueueStatus.IN_PROGRESS)
                )
                .orElseThrow(() -> new RuntimeException("No active queue found"));

        QueueResponseDto dto = modelMapper.map(entry, QueueResponseDto.class);
        dto.setAppointmentId(entry.getAppointment().getId());
        dto.setStatus(entry.getStatus().name());

        return List.of(dto);
    }
}
