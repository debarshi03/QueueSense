package com.example.QueueSense.QueueSense.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Testing {

    @GetMapping("/test")
    public String testApi() {
        return "Protected API working";
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/queue/1")
    public String sendMessage(String message){

        System.out.println("Received: " + message);

        return message;
    }
}
