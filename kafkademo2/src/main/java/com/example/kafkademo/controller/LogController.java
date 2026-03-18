package com.example.kafkademo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafkademo.model.LogEvent;
import com.example.kafkademo.service.LogProducerService;


@RestController
@RequestMapping("/log")
public class LogController {

    private final LogProducerService logProducerService;

    public LogController(LogProducerService logProducerService) {
        this.logProducerService = logProducerService;
    }

    @PostMapping
    public String createLog(@RequestBody LogEvent log){

        logProducerService.publishLog(log);

        return "Log sent to Kafka";
    }
}