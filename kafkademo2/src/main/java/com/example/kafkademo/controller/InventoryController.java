package com.example.kafkademo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kafkademo.service.LogProducerService;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final LogProducerService logProducer;

    public InventoryController(LogProducerService logProducer) {
        this.logProducer = logProducer;
    }

    @PostMapping("/update")
    public String updateStock(@RequestParam String customerId){

        logProducer.publishLog(
                customerId,
                "InventoryService",
                "INFO",
                "Inventory updated"
        );

        return "Stock updated";
    }
}