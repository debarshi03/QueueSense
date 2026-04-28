package com.example.QueueSense.QueueSense.config;

import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.entity.type.RoleType;
import com.example.QueueSense.QueueSense.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdmin() {

        if (userRepository.findByUsername("admin").isEmpty()) {

            User admin = User.builder()
                    .name("Admin")
                    .username("admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of(RoleType.ADMIN))
                    .phone("9330674456")
                    .build();

            userRepository.save(admin);

            System.out.println("✅ Admin user created successfully!");
        } else {
            System.out.println("⚡ Admin already exists");
        }
    }
}