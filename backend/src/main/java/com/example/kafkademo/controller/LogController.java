package com.example.kafkademo.controller;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafkademo.model.LogEvent;
import com.example.kafkademo.service.LogProducerService;

@RestController
@RequestMapping("/log")
@CrossOrigin
public class LogController {

    private final LogProducerService logProducerService;
    private final Random random = new Random();
    private final AtomicBoolean streaming = new AtomicBoolean(false);

    private final String[] services  = { "OrderService", "PaymentService", "AuthService", "InventoryService" };
    private final String[] levels    = { "INFO", "WARN", "ERROR" };
    private final String[] customers = { "CUST-1001", "CUST-1002", "CUST-1003" };

    public LogController(LogProducerService logProducerService) {
        this.logProducerService = logProducerService;
    }

    @PostMapping
    public String createLog(@RequestBody LogEvent log) {
        logProducerService.publishLog(log);
        return "Log sent to Kafka";
    }

    @GetMapping("/generate")
    public String generateLog() {
        logProducerService.publishLog(buildRandomLog());
        return "Log generated";
    }

    @GetMapping("/stream")
    public String streamLogs() {

        if (streaming.get()) {
            return "Already streaming";
        }

        streaming.set(true);

        Thread thread = new Thread(() -> {
            while (streaming.get()) {
                try {
                    logProducerService.publishLog(buildRandomLog());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Stream error: " + e.getMessage());
                }
            }
            System.out.println("Streaming stopped.");
        });

        thread.setDaemon(true);
        thread.start();

        return "Streaming started";
    }

    @GetMapping("/stream/stop")
    public String stopStream() {
        streaming.set(false);
        return "Streaming stopped";
    }

    private LogEvent buildRandomLog() {

        String service  = services[random.nextInt(services.length)];
        String level    = levels[random.nextInt(levels.length)];
        String customer = customers[random.nextInt(customers.length)];

        String message;
        String details;

        switch (service) {

            case "OrderService" -> {
                String orderId = "ORD-" + random.nextInt(10000);
                message = level.equals("ERROR") ? "Order creation failed" : "Order created successfully";
                details = "OrderId=" + orderId;
            }

            case "PaymentService" -> {
                String txnId  = "TXN-" + System.currentTimeMillis();
                int    amount = 1000 + random.nextInt(9000);
                message = level.equals("ERROR") ? "Payment failed"
                        : level.equals("WARN")  ? "Payment pending"
                        : "Payment successful";
                details = "TxnId=" + txnId + " | Amount=" + amount;
            }

            case "AuthService" -> {
                String userId = "USR-" + random.nextInt(5000);
                message = level.equals("ERROR") ? "User authentication failed" : "User login successful";
                details = "UserId=" + userId;
            }

            default -> {
                String productId = "PRD-" + random.nextInt(1000);
                int    stock     = random.nextInt(20);
                message = (stock < 5) ? "Low stock alert" : "Inventory updated";
                details = "ProductId=" + productId + " | Stock=" + stock;
            }
        }

        LogEvent log = new LogEvent();
        log.setTimestamp(LocalDateTime.now().toString());
        log.setCustomerId(customer);
        log.setServiceName(service);
        log.setLogLevel(level);
        log.setMessage(message);
        log.setDetails(details);

        return log;
    }
}