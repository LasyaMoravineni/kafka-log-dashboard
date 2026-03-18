package com.example.kafkademo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafkademo.service.LogProducerService;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final LogProducerService logProducer;

    public PaymentController(LogProducerService logProducer) {
        this.logProducer = logProducer;
    }

    @PostMapping
    public String processPayment(@RequestParam String customerId){

        logProducer.publishLog(
                customerId,
                "PaymentService",
                "INFO",
                "Payment processed successfully"
        );

        return "Payment successful";
    }
}