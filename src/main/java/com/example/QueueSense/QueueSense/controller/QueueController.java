package com.example.QueueSense.QueueSense.controller;

import com.example.QueueSense.QueueSense.dto.QueueResponseDto;
import com.example.QueueSense.QueueSense.entity.User;
import com.example.QueueSense.QueueSense.queue.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;


    @GetMapping("/provider")
    public ResponseEntity<List<QueueResponseDto>> getQueueByProvider() {
        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(queueService.getQueueByProvider(user.getId()));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QueueResponseDto>> getUserQueue( ){
        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(queueService.getUserQueue(user.getId()));
    }


//    @GetMapping("/{queueId}")
//    public QueueResponseDto getQueueById(@PathVariable Long queueId) {
//        return queueService.getQueueById(queueId);
//    }

}
