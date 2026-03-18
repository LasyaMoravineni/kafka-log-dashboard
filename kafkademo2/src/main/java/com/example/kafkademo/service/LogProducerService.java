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

    // Used by controllers
    public void publishLog(String customerId, String serviceName, String level, String message){

        LogEvent log = new LogEvent();

        log.setTimestamp(LocalDateTime.now().toString());
        log.setCustomerId(customerId);
        log.setServiceName(serviceName);
        log.setLogLevel(level);
        log.setMessage(message);

        sendToKafka(log);
    }

    // Used by /log endpoint
    public void publishLog(LogEvent log){
        sendToKafka(log);
    }

    private void sendToKafka(LogEvent log){

        System.out.println("Sending log to Kafka: " + log);

        kafkaTemplate.send("logs-topic", log.getServiceName(), log);
    }
}