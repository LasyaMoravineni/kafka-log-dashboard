package com.example.kafkademo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafkademo.model.Order;
import com.example.kafkademo.service.LogProducerService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final LogProducerService logProducerService;

    public OrderController(LogProducerService logProducer) {
        this.logProducerService = logProducer;
    }

    @PostMapping
    public String createOrder(@RequestBody Order order){

        logProducerService.publishLog(
                order.getCustomerId(),
                "OrderService",
                "INFO",
                "Order created: " + order.getOrderId()
        );

        return "Order created successfully";
    }
}