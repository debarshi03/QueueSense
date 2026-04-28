package com.example.QueueSense.QueueSense.controller;


import com.example.QueueSense.QueueSense.dto.OnboardServiceProviderRequestDto;
import com.example.QueueSense.QueueSense.dto.ServiceProviderResponseDto;
import com.example.QueueSense.QueueSense.service.ServiceProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ServiceProviderService serviceProviderService;

    @GetMapping("/providers")
    public ResponseEntity<List<ServiceProviderResponseDto>> getAllProviders(){
        return ResponseEntity.ok(serviceProviderService.getAllServiceProvider());
    }

    @PostMapping("/onBoardNewDoctor")
    public ResponseEntity<ServiceProviderResponseDto> onBoardRequest(@RequestBody OnboardServiceProviderRequestDto onboardServiceProviderRequestDto){
        return ResponseEntity.ok(serviceProviderService.onBoardProvider(onboardServiceProviderRequestDto));
    }
}
