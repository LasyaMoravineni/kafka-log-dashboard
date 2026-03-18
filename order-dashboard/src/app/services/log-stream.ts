import { Injectable } from '@angular/core';
import * as SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class LogStreamService {

  private stompClient!: Client;

  connect(customerId: string, logCallback: any, metricCallback: any){

    const socket = new SockJS.default('http://localhost:8080/logs');

    this.stompClient = new Client({
      webSocketFactory: () => socket as any,
      debug: (msg) => console.log(msg)
    });

    this.stompClient.onConnect = () => {

      console.log("WebSocket connected");

      // LOG STREAM
      this.stompClient.subscribe(`/topic/logs/${customerId}`, (message:any)=>{

        const log = JSON.parse(message.body);
        console.log("Log received:", log);

        logCallback(log);

      });

      // METRICS STREAM
      this.stompClient.subscribe('/topic/metrics', (message:any)=>{

        console.log("Metric received:", message.body);

        metricCallback(message.body);

      });

    };

    this.stompClient.activate();
  }

}