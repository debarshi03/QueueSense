package com.example.QueueSense.QueueSense.controller;

import com.example.QueueSense.QueueSense.dto.*;
import com.example.QueueSense.QueueSense.entity.Notification;
import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.queue.QueueService;
import com.example.QueueSense.QueueSense.service.AppointmentService;
import com.example.QueueSense.QueueSense.service.NotificationService;
import com.example.QueueSense.QueueSense.service.ServiceProviderService;
import com.example.QueueSense.QueueSense.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private final NotificationService notificationService;
    private final QueueService queueService;

    @PostMapping("/bookAppointment")
    public ResponseEntity<AppointmentResponseDto> createNewAppointment(@RequestBody CreateAppointmentRequestDto createAppointmentRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createNewAppointment(createAppointmentRequestDto));
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointment(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userService.getAllAppointments(user.getId()));
    }

    @GetMapping("/notification")
    public ResponseEntity<List<NotifiactionResponseDto>> getAllNotification(){
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(notificationService.getAllNotification(user.getId()));
    }

    @GetMapping("/wait-time/{appointmentId} ")
    public ResponseEntity<WaitTimeResponseDto> getWaitTime(@PathVariable Long appointmentId, Authentication authentication){
        User user=(User) authentication.getPrincipal();
        return ResponseEntity.ok(queueService.getWaitTime(appointmentId,user.getId()));
    }

    @PostMapping("/cancel-appointment")
    public ResponseEntity<AppointmentStatusResponseDto> cancelAppointment(@PathVariable AppointmentStatusRequestDto appointmentStatusRequestDto){
        return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentStatusRequestDto));
    }
}
