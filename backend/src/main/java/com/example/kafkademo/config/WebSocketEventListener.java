package com.example.kafkademo.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();
        if (sessionId == null) return;

        String clientId = accessor.getFirstNativeHeader("clientId");
        String service  = accessor.getFirstNativeHeader("service");

        String filter = "clientId=" + (clientId != null ? clientId : "")
                      + "&service="  + (service  != null ? service  : "");

        SessionFilterStore.sessionFilters.put(sessionId, filter);

        System.out.println("Session connected: " + sessionId + " | filter: " + filter);
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        if (sessionId != null) {
            SessionFilterStore.sessionFilters.remove(sessionId);
            System.out.println("Session disconnected and removed: " + sessionId);
        }
    }
}