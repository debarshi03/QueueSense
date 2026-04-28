package com.example.QueueSense.QueueSense.controller;

import com.example.QueueSense.QueueSense.dto.AppointmentResponseDto;
import com.example.QueueSense.QueueSense.dto.AppointmentStatusRequestDto;
import com.example.QueueSense.QueueSense.dto.AppointmentStatusResponseDto;
import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.service.AppointmentService;
import com.example.QueueSense.QueueSense.service.ServiceProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/provider")
@RequiredArgsConstructor
public class ServiceProviderController {
    private final ServiceProviderService serviceProviderService;
    private final AppointmentService appointmentService;

    @PostMapping("/statusUpdate")
    public ResponseEntity<AppointmentStatusResponseDto> updateStatus(@RequestBody AppointmentStatusRequestDto appointmentStatusRequestDto){
        return ResponseEntity.ok(appointmentService.updateStatus(appointmentStatusRequestDto));
    }

//    @GetMapping("/allAppointments")
//    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments(){
//        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return ResponseEntity.of(serviceProviderService.getAllAppointments(user.getId()));
//    }
}
