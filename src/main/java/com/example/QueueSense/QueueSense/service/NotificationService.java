package com.example.QueueSense.QueueSense.service;

import com.example.QueueSense.QueueSense.dto.NotifiactionResponseDto;
import com.example.QueueSense.QueueSense.entity.Notification;
import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.entity.type.NotificationType;
import com.example.QueueSense.QueueSense.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public void sendNotification(User user, String message) {
        Notification notification=Notification.builder()
                .user(user)
                .message(message)
                .type(NotificationType.SYSTEM)
                .build();
        notificationRepository.save(notification);
    }

    public @Nullable List<NotifiactionResponseDto> getAllNotification(Long id) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(id)
                .stream()
                .map(notification -> modelMapper.map(notification, NotifiactionResponseDto.class))
                .toList();

    }
}
