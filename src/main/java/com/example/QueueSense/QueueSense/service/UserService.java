package com.example.QueueSense.QueueSense.service;

import com.example.QueueSense.QueueSense.dto.AppointmentResponseDto;
import com.example.QueueSense.QueueSense.entity.Appointment;
import com.example.QueueSense.QueueSense.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.Module;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;



    public @Nullable List<AppointmentResponseDto> getAllAppointments(Long id) {

        return appointmentRepository.findByUserId(id)
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDto.class))
                .toList();
    }

    public @Nullable AppointmentResponseDto getAppointment(Long id, Long appointmentId) {
        Appointment appointment=appointmentRepository
                .findByIdAndUser_Id(
                        appointmentId,
                        id
                ).orElseThrow(()->
                        new RuntimeException(
                                "Appointment not found"
                        )
                );

        return modelMapper.map(appointment, AppointmentResponseDto.class);
    }
}
