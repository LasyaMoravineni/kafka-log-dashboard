package com.example.kafkademo.config;

import java.util.concurrent.ConcurrentHashMap;

public class SessionFilterStore {

    public static ConcurrentHashMap<String, String> sessionFilters = new ConcurrentHashMap<>();
}