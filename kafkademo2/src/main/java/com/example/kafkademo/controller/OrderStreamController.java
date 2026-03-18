package com.example.kafkademo.controller;

import com.example.kafkademo.consumer.OrderConsumer;
import com.example.kafkademo.model.Order;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stream")
@CrossOrigin
public class OrderStreamController {

    private final OrderConsumer consumer;

    public OrderStreamController(OrderConsumer consumer) {
        this.consumer = consumer;
    }

    @GetMapping("/orders")
    public List<Order> getOrders() {
        return consumer.getOrders();
    }
}