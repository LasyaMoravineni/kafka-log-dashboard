import { Injectable } from '@angular/core';
import * as SockJS from 'sockjs-client';
import { Client, StompSubscription } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class LogStreamService {

  private stompClient!: Client;
  private logSubscription!: StompSubscription;

  connect(
    clientId: string,
    serviceName: string,
    logCallback: (log: any) => void,
    metricCallback: (metric: string) => void
  ) {

    const socket = new SockJS.default('http://localhost:8080/logs');

    this.stompClient = new Client({
      webSocketFactory: () => socket as any,

      // ✅ Filters sent as STOMP connect headers
      // Backend reads these via getFirstNativeHeader() in SessionConnectEvent
      connectHeaders: {
        clientId: clientId,
        service:  serviceName
      },

      debug: (msg) => console.log(msg)
    });

    this.stompClient.onConnect = () => {

      console.log('WebSocket connected');

      // ✅ Subscribe to the per-filter topic
      // Must match exactly what LogConsumer builds on the backend
      const clientSegment  = (clientId    && clientId.trim()    !== '') ? clientId    : 'ALL';
      const serviceSegment = (serviceName && serviceName.trim() !== '') ? serviceName : 'ALL';
      const topic = `/topic/logs/${clientSegment}/${serviceSegment}`;

      console.log('Subscribing to:', topic);

      this.logSubscription = this.stompClient.subscribe(
        topic,
        (message) => {
          const log = JSON.parse(message.body);
          console.log('Log received:', log);
          logCallback(log);
        }
      );

      // Metrics broadcast — all sessions receive this regardless of filter
      this.stompClient.subscribe('/topic/metrics', (message) => {
        metricCallback(message.body);
      });
    };

    this.stompClient.activate();
  }

  // ✅ Called when user changes filters after connecting
  // STOMP headers can only be sent at connect time so we fully reconnect
  resubscribe(
    clientId: string,
    serviceName: string,
    logCallback: (log: any) => void,
    metricCallback: (metric: string) => void
  ) {
    this.disconnect();
    setTimeout(() => {
      this.connect(clientId, serviceName, logCallback, metricCallback);
    }, 300);
  }

  // ✅ Cleanly closes the socket — triggers SessionDisconnectEvent on backend
  disconnect() {
    if (this.stompClient && this.stompClient.active) {
      this.stompClient.deactivate();
      console.log('WebSocket disconnected');
    }
  }
}