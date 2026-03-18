import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private api = "http://localhost:8080/stream/orders";

  constructor(private http: HttpClient) {}

  getOrders(){
    return this.http.get<any[]>(this.api);
  }
}