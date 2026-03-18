package com.example.kafkademo.model;


public class LogEvent {

    private String timestamp;
    private String customerId;
    private String serviceName;
    private String logLevel;
    private String message;

    public LogEvent() {}

    public LogEvent(String timestamp, String customerId, String serviceName, String logLevel, String message) {
        this.timestamp = timestamp;
        this.customerId = customerId;
        this.serviceName = serviceName;
        this.logLevel = logLevel;
        this.message = message;
    }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getLogLevel() { return logLevel; }
    public void setLogLevel(String logLevel) { this.logLevel = logLevel; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}