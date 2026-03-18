package com.example.kafkademo.service;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LogStreamProcessor {

    @Bean
    public KStream<String, String> processLogs(StreamsBuilder builder) {

        KStream<String, String> stream = builder.stream("logs-topic");

        stream.to("ui-logs-topic");

        KTable<String, Long> serviceCounts =
                stream
                        .mapValues(value -> extractService(value))
                        .groupBy((key, service) -> service)
                        .count();

        serviceCounts.toStream()
                .map((service, count) ->
                        KeyValue.pair(service, service + ":" + count))
                .to("service-metrics-topic");

        return stream;
    }

    private String extractService(String logJson){

        if(logJson.contains("OrderService")) return "OrderService";
        if(logJson.contains("PaymentService")) return "PaymentService";
        if(logJson.contains("AuthService")) return "AuthService";
        if(logJson.contains("InventoryService")) return "InventoryService";

        return "Unknown";
    }
}