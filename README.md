🚀 Kafka Real-Time Log Monitoring Dashboard

A real-time transaction log monitoring system built using Spring Boot, Apache Kafka, Kafka Streams, WebSockets, and Angular.
This project demonstrates a scalable, event-driven architecture where logs are processed, filtered at backend, and streamed live to UI dashboards.

📌 Features

* 🔄 Real-time log streaming using WebSockets (STOMP)
* 🎯 Backend-level filtering (client ID + service name)
* 🧑‍💻 Session-based log isolation (per user connection)
* 📊 Live analytics using Kafka Streams
* 📈 Interactive charts (logs/sec, service distribution, log levels)
* ⚡ Efficient streaming (UI receives only relevant logs)
* ⏯️ Start/Stop live streaming
* 🧩 Event-driven, scalable architecture

🧠 Tech Stack

Backend
* Spring Boot
* Apache Kafka
* Kafka Streams
* Spring WebSocket (STOMP)
Frontend
* Angular
* Chart.js
* WebSocket (SockJS + STOMP)
* 

🏗️ Architecture Diagram

￼<img width="1020" height="511" alt="KafkaFlow drawio" src="https://github.com/user-attachments/assets/9c9746c4-06fa-4e91-9f50-e0909c7331ba" />




🔄 Flow Explanation

+---------------------+
|   Log Producer      |
| (Spring Boot)       |
+----------+----------+
           |
           ▼
+---------------------+
| Kafka (logs-topic)  |
+----------+----------+
           |
           ▼
+-----------------------------+
| Kafka Streams Processor     |
| - Forwards logs             |
| - Generates metrics         |
+----------+------------------+
           | 
   +-------+--------+
   |                |
   ▼                ▼
ui-logs-topic   service-metrics-topic
   |                |
   ▼                ▼
+---------------------------+     +-----------------------------+
| LogConsumer               |     | MetricsConsumer             |
| (reads ui-logs-topic)     |     | (reads metrics topic)       |
+------------+--------------+     +-------------+---------------+
             |                                  |
             ▼                                  ▼
      WebSocket Layer (STOMP)         Broadcast metrics to all
      - Stores session filters
      - Filters logs per session
      - Sends to topic paths
             |
             ▼
+----------------------------------+
| Angular Dashboard (UI)           |
| - Receives filtered logs         |
| - Displays charts + table        |
+----------------------------------+




1️⃣ Log Generation
Logs are generated in backend:

OrderService → "Order created successfully"
PaymentService → "Payment failed"
AuthService → "User login successful"

Each log contains:

{
  "timestamp": "...",
  "customerId": "CUST-1001",
  "serviceName": "OrderService",
  "logLevel": "INFO",
  "message": "Order created successfully",
  "details": "OrderId=ORD-1234"
}

Logs are sent to:

Kafka → logs-topic


2️⃣ Stream Processing (Kafka Streams)
Kafka Streams processes logs:
* Forwards logs → ui-logs-topic
* Aggregates counts → service-metrics-topic

logs-topic → ui-logs-topic
           → service-metrics-topic


3️⃣ Backend Filtering (Key Feature)
Logs are consumed by:

LogConsumer.java



How filtering works:
1. User selects filters (client + service)
2. WebSocket connection is established
3. Backend stores filters per session:

sessionId → (customerId, serviceName)

1. For each log:

Compare log.customerId with session.customerId
Compare log.serviceName with session.serviceName

1. If match → send to that user only

Example:

Log → CUST-1001 + OrderService

Session A → CUST-1001 + OrderService → ✅ sent
Session B → CUST-1002 + PaymentService → ❌ ignored


4️⃣ WebSocket Delivery
Logs are pushed using:

/topic/logs/{customerId}/{serviceName}

Metrics are broadcast to:

/topic/metrics


5️⃣ UI Subscription
Angular subscribes to:

/topic/logs/{client}/{service}
/topic/metrics


6️⃣ UI Rendering
Dashboard displays:
✔ Table
* Time
* Client ID
* Service
* Log Level
* Message
* Details

✔ Charts
* Log Level Distribution
* Service Distribution
* Logs/sec (real-time)



🎯 Filtering Architecture (Important)
Layer	Role
Kafka	Stores all logs
Consumer	Reads logs
Backend	Applies session-based filtering
WebSocket	Routes filtered logs
UI	Displays only received logs


⚖️ Why Backend Filtering?

Instead of:

Send all logs → Filter in UI ❌

We use:

Filter in backend → Send only required logs ✅

Benefits:
* Reduced network load
* Better scalability
* Cleaner UI logic
* Real-time efficiency



📊 Dashboard Features

✔ Stats
* Total Logs
* Logs/sec
* Error count
* Warning count

✔ Charts
* Log Level Distribution
* Service Distribution
* Logs/sec Timeline

✔ Table
* Time
* Client
* Service
* Level
* Message
* Details



▶️ How to Run

1️⃣ Start Kafka & Zookeeper

bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties


2️⃣ Run Backend

cd backend
mvn spring-boot:run


3️⃣ Run Frontend

cd frontend
ng serve

Open:

http://localhost:4200


4️⃣ Start Log Streaming

http://localhost:8080/log/stream


🎮 How to Use
1. Select Client ID + Service
2. Click Connect
3. Logs start streaming
4. Change filters → auto re-subscribe
5. Observe real-time updates

🚀 Key Highlights
* Real-time Kafka streaming system
* Backend-level filtering (important feature)
* WebSocket-based delivery
* Kafka Streams for analytics
* Clean separation of concerns

🧠 Learnings
* Kafka producers, consumers, and streams
* Real-time data processing
* WebSocket-based communication
* Backend vs UI filtering design
* Event-driven architecture
