package com.example.kafkademo.config;

import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/logs")
            .setAllowedOriginPatterns("*")
            .addInterceptors(new HandshakeInterceptor() {

                @Override
                public boolean beforeHandshake(
                        org.springframework.http.server.ServerHttpRequest request,
                        org.springframework.http.server.ServerHttpResponse response,
                        org.springframework.web.socket.WebSocketHandler wsHandler,
                        Map<String, Object> attributes) {

                    String query = request.getURI().getQuery();
                    if (query != null) {
                        attributes.put("query", query);
                    }
                    return true;
                }

                @Override
                public void afterHandshake(
                        org.springframework.http.server.ServerHttpRequest request,
                        org.springframework.http.server.ServerHttpResponse response,
                        org.springframework.web.socket.WebSocketHandler wsHandler,
                        Exception exception) {}
            })
            .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}