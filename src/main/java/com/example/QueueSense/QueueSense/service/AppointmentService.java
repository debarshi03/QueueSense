package com.example.QueueSense.QueueSense.service;

import com.example.QueueSense.QueueSense.dto.AppointmentResponseDto;
import com.example.QueueSense.QueueSense.dto.AppointmentStatusRequestDto;
import com.example.QueueSense.QueueSense.dto.AppointmentStatusResponseDto;
import com.example.QueueSense.QueueSense.dto.CreateAppointmentRequestDto;
import com.example.QueueSense.QueueSense.entity.Appointment;
import com.example.QueueSense.QueueSense.entity.QueueEntry;
import com.example.QueueSense.QueueSense.entity.ServiceProvider;
import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.entity.type.AppointmentStatus;
import com.example.QueueSense.QueueSense.entity.type.QueueStatus;
import com.example.QueueSense.QueueSense.queue.QueueService;
import com.example.QueueSense.QueueSense.repository.AppointmentRepository;
import com.example.QueueSense.QueueSense.repository.QueueRepository;
import com.example.QueueSense.QueueSense.repository.ServiceProviderRepository;
import com.example.QueueSense.QueueSense.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;
    private final ServiceProviderRepository serviceProviderRepository;
    private final QueueService queueService;
    private final QueueRepository queueRepository;


    public @Nullable AppointmentStatusResponseDto updateStatus(AppointmentStatusRequestDto appointmentStatusRequestDto) {
        Appointment appointment = appointmentRepository.findById(appointmentStatusRequestDto.getAppointmentId())
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (appointment.getStatus() == appointmentStatusRequestDto.getStatus()) {
            throw new IllegalArgumentException("Appointment already has status: " + appointmentStatusRequestDto.getStatus());
        }


        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            QueueEntry queueEntry=queueRepository.findByAppointment(appointment)
                    .orElseThrow(()->new IllegalArgumentException("Cannot change status after completion"));
            queueEntry.setStatus(QueueStatus.COMPLETED);
            queueRepository.save(queueEntry);

            queueService.recalculateQueue(appointment.getProvider().getAverageServiceTime());
        }


        if (appointment.getStatus() == AppointmentStatus.BOOKED &&
                appointmentStatusRequestDto.getStatus() != AppointmentStatus.COMPLETED) {

            throw new IllegalArgumentException("Invalid status transition");
        }
        appointment.setStatus(appointmentStatusRequestDto.getStatus());
        appointmentRepository.save(appointment);

        AppointmentStatusResponseDto responseDto=new AppointmentStatusResponseDto();
        responseDto.setAppointmentId(appointment.getId());
        responseDto.setStatus(appointment.getStatus());
        responseDto.setMessage("Status is updated Successfully");
        responseDto.setUpdatedAt(LocalDateTime.now());

        return responseDto;
    }

    public @Nullable AppointmentResponseDto createNewAppointment(CreateAppointmentRequestDto createAppointmentRequestDto) {
        Long providerId=createAppointmentRequestDto.getProviderId();
        Long userId=createAppointmentRequestDto.getUserId();
        User user= userRepository.findById(userId).orElseThrow();
        ServiceProvider serviceProvider=serviceProviderRepository.findById(providerId).orElseThrow();

        Appointment appointment = Appointment.builder()
                .reason(createAppointmentRequestDto.getReason())
                .appointmentTime(createAppointmentRequestDto.getAppointmentTime())
                .status(AppointmentStatus.BOOKED)
                .build();

        appointment.setProvider(serviceProvider);
        appointment.setUser(user);
        user.getAppointments().add(appointment);

        appointment = appointmentRepository.save(appointment);

        queueService.addToQueue(appointment,serviceProvider.getAverageServiceTime());

        return modelMapper.map(appointment, AppointmentResponseDto.class);



    }
}
