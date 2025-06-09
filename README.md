# 🧾 Product Price API

## Overview

Product Price API is a reactive, high-performance Java 17 application that retrieves product prices based on the specified application date, product ID, and brand ID.  
It is built with Spring Boot 3, Spring WebFlux, and R2DBC following hexagonal architecture and SOLID principles.

The system exposes:

- A **REST endpoint**: `GET /api/prices/filter`
- A **gRPC service**: `getPrice`

The solution includes advanced features:

- **Event Auditing**: Stores price query events in the `PRICE_EVENTS` table and publishes them to a Kafka topic (`priceTopic`)
- **Fault Tolerance**: Resilience4j with circuit breaker
- **Observability**: OpenTelemetry with Zipkin
- **In-Memory H2**: Used for storing prices and events
- **In-Memory H2**: Used for storing prices and events
- **Swagger**: Used to interact with and explore the available API endpoints.

---

## ✅ Test Scenarios

The API returns the correct price in the following cases:

| Date and Time       | Product ID | Brand ID | Expected Price | Price List |
|---------------------|------------|----------|----------------|------------|
| 2020-06-14 10:00:00 | 35455      | 1        | 35.50 EUR      | 1          |
| 2020-06-14 16:00:00 | 35455      | 1        | 25.45 EUR      | 2          |
| 2020-06-14 21:00:00 | 35455      | 1        | 35.50 EUR      | 1          |
| 2020-06-15 10:00:00 | 35455      | 1        | 30.50 EUR      | 3          |
| 2020-06-16 21:00:00 | 35455      | 1        | 38.95 EUR      | 4          |

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd product-price-api
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Start dependencies with Docker

Kafka and Zipkin must be running. Use Docker Compose:

```bash
docker-compose up -d
```

- Kafka: localhost:29092
- Zipkin: http://localhost:9411

### 4. Run the application

```bash
mvn spring-boot:run
```

Services started:

- REST API: [http://localhost:8080/api/prices/filter](http://localhost:8080/api/prices/filter)
- gRPC: Port `9090`
- H2 Database Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

---

## 🔍 Example Usage

### REST Call

```bash
curl "http://localhost:8080/api/prices/filter?productId=35455&brandId=1&date=14/06/2020&time=10:00"
```

Response:

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 1,
  "startDate": "14/06/2020 00:00:00",
  "endDate": "31/12/2020 23:59:59",
  "price": 35.5
}
```

### gRPC Call

Use a client like BloomRPC with `price.proto`:

```proto
syntax = "proto3";

option java_package = "org.inditex.prices.infrastructure.adapter.grpc";
option java_multiple_files = true;

service PriceService {
  rpc getPrice (PriceRequest) returns (PriceResponse);
}

message PriceRequest {
  int64 product_id = 1;
  int64 brand_id = 2;
  string date = 3; // Format: dd/MM/yyyy
  string time = 4; // Format: HH:mm
}

message PriceResponse {
  int64 product_id = 1;
  int64 brand_id = 2;
  int32 price_list = 3;
  string start_date = 4;
  string end_date = 5;
  double price = 6;
}
```

Example gRPC request:

```json
{
  "product_id": 35455,
  "brand_id": 1,
  "date": "14/06/2020",
  "time": "10:00"
}
```

---

## 📊 Monitoring and Observability

  JDBC URL: `jdbc:h2:mem:testdb`  
  User: `sa` | Password: *(empty)*

- **Kafka**: Topic `priceTopic`  
  Use a local Kafka consumer on `localhost:29092`

- **Zipkin**: [http://localhost:9411](http://localhost:9411)

---

## 🧪 Testing

Run all tests:

```bash
mvn test
```

Generate coverage report:

```bash
mvn jacoco:report
```

View the report at: `target/site/jacoco/index.html`

---

## 🧱 Architecture
Hexagonal

**Domain Layer** (`org.inditex.prices.domain`)
- Models: `Price`, `PriceEvent`
- Ports: `PriceServicePort`, `EventStorePort`
- Use Cases: `FindApplicablePriceUseCase`, `StorePriceEventUseCase`

**Application Layer** (`org.inditex.prices.application`)
- Services: `PriceService - > business logic`
- DTOs: `PriceRequestDto`, `PriceResponseDto`
- Mapper: `PriceMapper`

**Infrastructure Layer (org.inditex.prices.infrastructure)

- REST Endpoints:
- PriceController: Exposes REST APIs for price queries, located in adapter/in/rest.
- GlobalExceptionHandler: Manages exceptions for REST responses, ensuring consistent error handling.
- gRPC Endpoints:
- PriceGrpcServiceAdapter: Implements gRPC service for price queries, located in adapter/in/grpc.
- Repositories (R2DBC):
    - PriceRepository: Interface for accessing price data in the database.
    - PriceEventRepository: Interface for accessing price event data.
    - PriceRepositoryAdapter: Adapts the PriceRepository to the domain model.
    - EventStoreRepositoryAdapter: Adapts the PriceEventRepository for event storage.

- Kafka:
    - KafkaEventPublisherAdapter: Publishes price events to Kafka topics, located in adapter/out/kafka.
- Entities:
    - PriceEntity: Represents the price table in the database.
    - PriceEventEntity: Represents the price event table in the database.
- Mappers:
    - PriceEntityMapper: Maps between PriceEntity and domain models.
    - PriceEventEntityMapper: Maps between PriceEventEntity and domain models.
- Resilience:
    - Resilience4jAdapter: Implements circuit breaker and retry patterns using Resilience4j, located in adapter/out/resilience.
- Tracing:
    - OpenTelemetryTracingAdapter: Integrates OpenTelemetry for distributed tracing, located in adapter/out/trace.

C- onfigurations:
    - KafkaReactiveConfig: Configures reactive Kafka producers and consumers.
    - ModelMapperConfig: Sets up ModelMapper for object mapping. 
    - OpenApiConfig: Configures OpenAPI/Swagger for REST API documentation.
    - ResilienceConfig: Configures Resilience4j properties.
    - TracingConfig: Configures OpenTelemetry tracing settings.

---

## 🧰 Technologies

| Technology     | Purpose                               |
|----------------|----------------------------------------|
| Java 17        | Modern LTS version                     |
| Spring Boot 3  | Main framework                         |
| Spring WebFlux | Reactive REST support                  |
| R2DBC          | Reactive DB access                     |
| H2  - RD2      | In-memory database                     |
| gRPC           | Remote procedure calls                 |
| Kafka          | Event publishing                       |
| Resilience4j   | Circuit breaker for fault tolerance    |
| OpenTelemetry  | Distributed tracing                    |
| Zipkin         | Visualization of traces                |
| JUnit 5        | Testing framework                      |
| Mockito        | Mocking framework                      |

---

## 📚 Documentation

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Kafka Events**: `price-events-asyncapi.yml`

---

## ⚠️ Disclaimers

This is a technical evaluation project.  
Advanced features like Kafka, gRPC, and Zipkin are **optional** but demonstrate advanced capabilities.  
Core REST functionality works standalone.  
Kafka (`localhost:29092`) and Zipkin (`localhost:9411`) must be running for full functionality.  
This repository may not be maintained after submission.

---

## 📄 License

MIT License.  
External dependencies (Kafka, Zipkin, etc.) are subject to their own licenses.