package com.example.kafkademo.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

// import com.example.kafkademo.model.LogEvent;

@Service
public class LogConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    
    private String extractCustomerId(String logJson){

        if(logJson.contains("1001")) return "1001";
        if(logJson.contains("1002")) return "1002";
        if(logJson.contains("1003")) return "1003";

        return "UnknownCustomer";
    }

    public LogConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(
            topics = "ui-logs-topic", groupId = "ui-log-group"
    )
    public void consume(String log) {

        System.out.println("Consumed log: " + log);
        String customerId = extractCustomerId(log);

        messagingTemplate.convertAndSend(
                "/topic/logs/"+ customerId,
                log
        );
    }

}
