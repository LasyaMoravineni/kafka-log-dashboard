package com.example.kafkademo.service;

import java.time.LocalDateTime;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.kafkademo.model.LogEvent;

@Service
public class LogProducerService {

    private final KafkaTemplate<String, LogEvent> kafkaTemplate;

    public LogProducerService(KafkaTemplate<String, LogEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishLog(String customerId, String serviceName, String level, String message) {

        LogEvent log = new LogEvent();
        log.setTimestamp(LocalDateTime.now().toString());
        log.setCustomerId(customerId);
        log.setServiceName(serviceName);
        log.setLogLevel(level);
        log.setMessage(message);

        sendToKafka(log);
    }

    public void publishLog(LogEvent log) {
        sendToKafka(log);
    }

    private void sendToKafka(LogEvent log) {

        if (log.getMessage() == null || log.getMessage().isEmpty()) {
            log.setMessage("Log event");
        }

        System.out.println("Sending to Kafka: " + log.getCustomerId()
            + " | " + log.getServiceName()
            + " | " + log.getLogLevel());

        kafkaTemplate.send("logs-topic", log.getCustomerId(), log);
    }
}