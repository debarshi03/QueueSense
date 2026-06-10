package com.example.QueueSense.QueueSense.service;

import com.example.QueueSense.QueueSense.dto.NotifiactionResponseDto;
import com.example.QueueSense.QueueSense.dto.WebSocketNotificationDto;
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
    private final WebSocketService webSocketService;

    public void sendNotification(User user, String message) {
        Notification notification=Notification.builder()
                .user(user)
                .message(message)
                .type(NotificationType.SYSTEM)
                .build();
        notificationRepository.save(notification);

        WebSocketNotificationDto dto =WebSocketNotificationDto.builder()
                .userId(user.getId())
                .message(message)
                .build();

        webSocketService.sendNotification(user.getId(), dto);

    }

    public @Nullable List<NotifiactionResponseDto> getAllNotification(Long id) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(id)
                .stream()
                .map(notification -> modelMapper.map(notification, NotifiactionResponseDto.class))
                .toList();

    }
//public List<NotifiactionResponseDto> getAllNotification(Long id) {
//
//    List<Notification> notifications =
//            notificationRepository
//                    .findByUser_IdOrderByCreatedAtDesc(id);
//
//    notifications.forEach(n ->
//            System.out.println(
//                    "DB Notification -> " +
//                            "notificationId: " + n.getId() +
//                            " userId: " + n.getUser().getId()
//            )
//    );
//
//    return notifications.stream()
//            .map(notification ->
//                    modelMapper.map(
//                            notification,
//                            NotifiactionResponseDto.class
//                    ))
//            .toList();
//}
}
