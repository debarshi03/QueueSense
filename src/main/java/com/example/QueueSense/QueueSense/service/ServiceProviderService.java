package com.example.QueueSense.QueueSense.service;

import com.example.QueueSense.QueueSense.dto.*;
import com.example.QueueSense.QueueSense.entity.Appointment;
import com.example.QueueSense.QueueSense.entity.ServiceProvider;
import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.entity.type.AppointmentStatus;
import com.example.QueueSense.QueueSense.entity.type.RoleType;
import com.example.QueueSense.QueueSense.repository.AppointmentRepository;
import com.example.QueueSense.QueueSense.repository.ServiceProviderRepository;
import com.example.QueueSense.QueueSense.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceProviderService {
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ModelMapper modelMapper;

    public List<ServiceProviderResponseDto> getAllServiceProvider(){
        return userRepository.findByRoles(RoleType.PROVIDER)
                .stream()
                .map(provider->modelMapper.map(provider, ServiceProviderResponseDto.class))
                .collect(Collectors.toList());
    }

    public ServiceProviderResponseDto onBoardProvider(OnboardServiceProviderRequestDto onboardServiceProviderRequestDto){
        User user=userRepository.findById(onboardServiceProviderRequestDto.getUserId()).orElseThrow();

        if (serviceProviderRepository.existsById(onboardServiceProviderRequestDto.getUserId())){
            throw new IllegalArgumentException("Already a Provider");
        }

        ServiceProvider serviceProvider=ServiceProvider.builder()
                .name(onboardServiceProviderRequestDto.getName())
                .email(onboardServiceProviderRequestDto.getEmail())
                .averageServiceTime(onboardServiceProviderRequestDto.getAverageServiceTime())
                .maxAppointment(onboardServiceProviderRequestDto.getMaxAppointment())
                .user(user)
                .build();

        user.getRoles().add(RoleType.PROVIDER);

        return modelMapper.map(serviceProviderRepository.save(serviceProvider), ServiceProviderResponseDto.class);
    }
    


    public List<AppointmentResponseDto> getAllAppointments(Long id) {

        return appointmentRepository.findByProviderId(id)
                .stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDto.class))
                .toList();
    }

}
