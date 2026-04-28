package com.example.QueueSense.QueueSense.controller;

import com.example.QueueSense.QueueSense.dto.AppointmentResponseDto;
import com.example.QueueSense.QueueSense.dto.CreateAppointmentRequestDto;
import com.example.QueueSense.QueueSense.dto.UserDto;
import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.service.AppointmentService;
import com.example.QueueSense.QueueSense.service.ServiceProviderService;
import com.example.QueueSense.QueueSense.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final ServiceProviderService serviceProviderService;
    private final AppointmentService appointmentService;
    private final UserService userService;

    @PostMapping("/bookAppointment")
    public ResponseEntity<AppointmentResponseDto> createNewAppointment(@RequestBody CreateAppointmentRequestDto createAppointmentRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createNewAppointment(createAppointmentRequestDto));
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointment(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getAllAppointments(user.getId()));
    }
}
