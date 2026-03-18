package com.example.kafkademo.controller;

import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafkademo.service.LogProducerService;

@RestController
public class DemoLogController {

    private final LogProducerService logProducer;

    public DemoLogController(LogProducerService logProducer) {
        this.logProducer = logProducer;
    }

    private final String[] services = {
            "OrderService",
            "PaymentService",
            "AuthService",
            "InventoryService"
    };

    private final String[] levels = {
            "INFO",
            "WARN",
            "ERROR"
    };

    private final String[] customers = {
            "1001","1002","1003"
    };

    private final String[] messages = {
            "Order created",
            "Payment processed",
            "User login success",
            "Inventory updated",
            "Payment failed",
            "Low inventory warning",
            "Authentication failed"
    };

    private final Random random = new Random();

    @GetMapping("/demo/generate")
    public String generateLogs() {

        for(int i=0; i<20; i++){

            String service = services[random.nextInt(services.length)];
            String level = levels[random.nextInt(levels.length)];
            String customer = customers[random.nextInt(customers.length)];
            String message = messages[random.nextInt(messages.length)];

            logProducer.publishLog(customer, service, level, message);
        }

        return "Generated 20 random logs";
    }

    @GetMapping("/demo/stream")
    public String streamLogs() {

        new Thread(() -> {
            try {

                for(int i=0;i<100;i++){

                    String service = services[random.nextInt(services.length)];
                    String level = levels[random.nextInt(levels.length)];
                    String customer = customers[random.nextInt(customers.length)];
                    String message = messages[random.nextInt(messages.length)];

                    logProducer.publishLog(customer, service, level, message);

                    Thread.sleep(1000);
                }

            } catch(Exception e){
                e.printStackTrace();
            }
        }).start();

        return "Streaming logs...";
    }
    }