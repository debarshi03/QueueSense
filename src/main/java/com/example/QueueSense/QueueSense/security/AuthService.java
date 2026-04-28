package com.example.QueueSense.QueueSense.security;

import com.example.QueueSense.QueueSense.dto.LoginRequestDto;
import com.example.QueueSense.QueueSense.dto.LoginResponseDto;
import com.example.QueueSense.QueueSense.dto.SignupRequestDto;
import com.example.QueueSense.QueueSense.dto.SignupResponseDto;
import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.entity.type.RoleType;
import com.example.QueueSense.QueueSense.repository.UserRepository;
import com.example.QueueSense.QueueSense.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;

    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        User user = (User) userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);
        if (user!=null) throw new IllegalArgumentException("User Alreay Exists");

        user = userRepository.save(User.builder()
                .name(signupRequestDto.getName())
                .username(signupRequestDto.getUsername())
                .email(signupRequestDto.getEmail())
                .phone(signupRequestDto.getPhone())
                        .roles(Set.of(RoleType.USER))
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .build()
        );

        return new SignupResponseDto(user.getId(),user.getUsername());
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),loginRequestDto.getPassword())
        );
        User user= (User) authentication.getPrincipal();
        String token=authUtil.ganerateAccessToken(user);
        return new LoginResponseDto(token,user.getId());
    }
}
