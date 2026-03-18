# 🚀 Kafka Log Monitoring Dashboard

A real-time **log monitoring and analytics dashboard** built using **Spring Boot, Apache Kafka, Kafka Streams, WebSockets, and Angular**.

This project demonstrates a **scalable, event-driven architecture** for monitoring logs across multiple microservices with **live UI updates and analytics**.

---

# 📌 Features

* 🔄 Real-time log streaming using WebSockets
* 🧑‍💻 Customer-based log isolation (multi-tenant)
* 📊 Live analytics using Kafka Streams
* 📈 Interactive charts (logs/sec, service distribution, log levels)
* 🎯 Service-based filtering in UI
* ⏯️ Start/Stop live log streaming
* ⚡ Scalable event-driven architecture

---

# 🧠 Tech Stack

### Backend

* Spring Boot
* Apache Kafka
* Kafka Streams
* Spring WebSocket (STOMP)

### Frontend

* Angular
* Chart.js
* WebSocket (SockJS + STOMP)

---

# 🏗️ Architecture Diagram

```
+---------------------+
|   Microservices     |
| (Order, Payment...) |
+----------+----------+
           |
           ▼
+---------------------+
|   Kafka (logs-topic)|
+----------+----------+
           |
           ▼
+-----------------------------+
| Kafka Streams Processor     |
| - Aggregation               |
| - Metrics generation        |
+----------+------------------+
           | 
   +-------+--------+
   |                |
   ▼                ▼
ui-logs-topic   service-metrics-topic
   |                |
   ▼                ▼
+----------------------------------+
| Spring Boot (LogConsumer)        |
| - Routes logs per customer       |
| - Sends via WebSocket            |
+----------------+-----------------+
                 |
                 ▼
     /topic/logs/{customerId}
     /topic/metrics
                 |
                 ▼
+----------------------------------+
| Angular Dashboard (UI)           |
| - Displays logs                  |
| - Charts & analytics             |
+----------------------------------+
```

---

# 🔄 Flow Explanation

## 1️⃣ Log Generation

Microservices generate logs:

```
OrderService → "Order created"
PaymentService → "Payment failed"
```

These logs are sent to:

```
Kafka → logs-topic
```

---

## 2️⃣ Stream Processing

Kafka Streams processes logs:

* Extracts service names
* Counts logs per service
* Generates metrics

```
logs-topic → ui-logs-topic
           → service-metrics-topic
```

---

## 3️⃣ Log Routing (Filtering)

Spring Boot consumes logs:

```
LogConsumer.java
```

Routes logs dynamically:

```
/topic/logs/{customerId}
```

👉 This ensures:

* Each customer only sees their logs
* Multi-tenant isolation

---

## 4️⃣ UI Subscription

Angular subscribes to:

```
/topic/logs/1001
/topic/metrics
```

👉 Based on selected customer

---

## 5️⃣ UI Rendering

Angular dashboard:

* Displays logs in table
* Updates charts in real-time
* Calculates logs/sec

---

# 🎯 Filtering Points

| Layer      | File             | Purpose                |
| ---------- | ---------------- | ---------------------- |
| Backend    | LogConsumer.java | Customer-based routing |
| WebSocket  | log-stream.ts    | Topic subscription     |
| Frontend   | order-table.ts   | Service filter         |
| UI Control | order-table.ts   | Start/Stop streaming   |

---

# 📊 Dashboard Features

### ✔ Stats Cards

* Total Logs
* Logs/sec
* Error count
* Warning count

### ✔ Charts

* Logs per Service (Bar Chart)
* Log Level Distribution (Doughnut)
* Service Distribution (Pie Chart)
* Logs/sec Timeline (Line Chart)

### ✔ Table

* Timestamp
* Customer ID
* Service Name
* Log Level
* Message

---

# 🧪 Sample Data

Example log:

```json
{
  "timestamp": "2026-03-10T12:30:11",
  "customerId": "1001",
  "serviceName": "OrderService",
  "logLevel": "INFO",
  "message": "Order created"
}
```

---

# ▶️ How to Run

## 1️⃣ Start Kafka & Zookeeper

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```

---

## 2️⃣ Run Backend

```bash
cd backend
mvn spring-boot:run
```

---

## 3️⃣ Run Frontend

```bash
cd frontend
ng serve
```

Open:

```
http://localhost:4200
```

---

# 🎮 How to Use

1. Select a **Customer ID (1001 / 1002 / 1003)**
2. Click **Connect Dashboard**
3. Generate logs (via API or random generator)
4. Watch logs update in real-time
5. Use:

   * Start/Stop buttons
   * Service filters

---

# 🚀 Key Highlights

* Real-time event-driven system
* Scalable Kafka-based architecture
* Multi-tenant log isolation
* Live analytics dashboard
* Production-style monitoring system

---

# 🧠 Learnings

* Kafka Streams for real-time analytics
* WebSocket-based UI updates
* Event-driven microservices architecture
* Handling real-time data in Angular

---

# 📌 Future Improvements

* Authentication & user roles
* Persistent storage (ElasticSearch / DB)
* Advanced filtering (time range, log level)
* Alerting system (email/SMS)
* Docker & Kubernetes deployment

