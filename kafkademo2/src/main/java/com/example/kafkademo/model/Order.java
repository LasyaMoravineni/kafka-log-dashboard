package com.example.kafkademo.model;

public class Order {

    private String orderId;
    private String customerId;
    private String product;

    public Order() {}

    public Order(String orderId, String customerId, String product) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.product = product;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getProduct() {
        return product;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}