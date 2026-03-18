package com.example.kafkademo.consumer;

import com.example.kafkademo.model.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderConsumer {

    private final List<Order> orders = new ArrayList<>();

    @KafkaListener(topics = "orders-topic", groupId = "order-group")
    public void consume(Order order) {

        orders.add(order);

        System.out.println("Received Order: " + order.getOrderId());
    }

    public List<Order> getOrders() {
        return orders;
    }
}