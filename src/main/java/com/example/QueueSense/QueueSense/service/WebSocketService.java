package com.example.QueueSense.QueueSense.service;


import com.example.QueueSense.QueueSense.dto.WebSocketNotificationDto;
import com.example.QueueSense.QueueSense.dto.WebSocketQueueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendQueueUpdate(Long providerId, WebSocketQueueDto dto){
        messagingTemplate.convertAndSend( "/topic/queue/" + providerId,dto);
    }

    public void sendNotification(Long userId, WebSocketNotificationDto dto){
        messagingTemplate.convertAndSend("/topic/notification/" + userId,dto);
    }
}
