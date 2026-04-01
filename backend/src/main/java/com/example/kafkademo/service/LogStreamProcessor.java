package com.example.kafkademo.service;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class LogStreamProcessor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public KStream<String, String> processLogs(StreamsBuilder builder) {

        KStream<String, String> stream = builder.stream("logs-topic");

        stream.to("ui-logs-topic");

        KTable<String, Long> serviceCounts = stream
                .mapValues(this::extractService)
                .groupBy((key, service) -> service)
                .count();

        serviceCounts.toStream()
                .map((service, count) ->
                        KeyValue.pair(service, service + ":" + count))
                .to("service-metrics-topic");

        return stream;
    }

    private String extractService(String logJson) {

        try {
            JsonNode node = objectMapper.readTree(logJson);
            String service = node.path("serviceName").asText("");
            return service.isEmpty() ? "Unknown" : service;
        } catch (Exception e) {
            System.err.println("extractService parse error: " + e.getMessage());
            return "Unknown";
        }
    }
}