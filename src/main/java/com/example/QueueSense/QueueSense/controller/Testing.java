package com.example.QueueSense.QueueSense.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Testing {

    @GetMapping("/test")
    public String testApi() {
        return "Protected API working";
    }
}
