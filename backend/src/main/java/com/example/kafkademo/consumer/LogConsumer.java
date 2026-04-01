package com.example.kafkademo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.kafkademo.config.SessionFilterStore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LogConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LogConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "ui-logs-topic", groupId = "ui-log-group")
    public void consume(ConsumerRecord<String, String> record) {

        try {

            String logJson  = record.value();
            JsonNode log    = objectMapper.readTree(logJson);

            String logClient  = log.path("customerId").asText("");
            String logService = log.path("serviceName").asText("");

            SessionFilterStore.sessionFilters.forEach((sessionId, query) -> {

                String filterClient  = extractParam(query, "clientId");
                String filterService = extractParam(query, "service");

                boolean clientMatch  = filterClient.isEmpty()  || filterClient.equals(logClient);
                boolean serviceMatch = filterService.isEmpty() || filterService.equals(logService);

                if (clientMatch && serviceMatch) {

                    String clientSegment  = filterClient.isEmpty()  ? "ALL" : filterClient;
                    String serviceSegment = filterService.isEmpty() ? "ALL" : filterService;
                    String topic = "/topic/logs/" + clientSegment + "/" + serviceSegment;

                    messagingTemplate.convertAndSend(topic, logJson);
                }
            });

        } catch (Exception e) {
            System.err.println("LogConsumer error: " + e.getMessage());
        }
    }

    private String extractParam(String query, String key) {

        if (query == null || !query.contains(key + "=")) return "";

        String[] parts = query.split(key + "=");

        if (parts.length < 2) return "";

        String after = parts[1];
        return after.contains("&") ? after.split("&")[0] : after;
    }
}