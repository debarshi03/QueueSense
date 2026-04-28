package com.example.QueueSense.QueueSense.controller;

import com.example.QueueSense.QueueSense.dto.LoginRequestDto;
import com.example.QueueSense.QueueSense.dto.LoginResponseDto;
import com.example.QueueSense.QueueSense.dto.SignupRequestDto;
import com.example.QueueSense.QueueSense.dto.SignupResponseDto;
import com.example.QueueSense.QueueSense.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @PostMapping("/signup")
    public  ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto){
        return ResponseEntity.ok(authService.signup(signupRequestDto));
    }
}
