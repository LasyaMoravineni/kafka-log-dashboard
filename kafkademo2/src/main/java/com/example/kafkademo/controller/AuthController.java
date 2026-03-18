package com.example.kafkademo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafkademo.service.LogProducerService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LogProducerService logProducer;

    public AuthController(LogProducerService logProducer) {
        this.logProducer = logProducer;
    }

    @PostMapping("/login")
    public String login(@RequestParam String customerId){

        logProducer.publishLog(
                customerId,
                "AuthService",
                "INFO",
                "User login successful"
        );

        return "Login successful";
    }
}