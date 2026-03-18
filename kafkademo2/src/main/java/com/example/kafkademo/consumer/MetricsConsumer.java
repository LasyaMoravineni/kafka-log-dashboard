package com.example.kafkademo.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MetricsConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    public MetricsConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "service-metrics-topic")
    public void consume(String metric) {

        System.out.println("Metric received: " + metric);

        messagingTemplate.convertAndSend("/topic/metrics", metric);
    }
}