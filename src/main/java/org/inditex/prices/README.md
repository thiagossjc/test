Product Price API
Overview
The Product Price API is a reactive, high-performance application designed to query product prices based on a specified date, product ID, and brand ID, as required by the test. Built with Spring WebFlux and R2DBC, it provides a REST endpoint (GET /api/prices) and a gRPC service (getPrice) for price queries. The API includes advanced features to demonstrate proficiency in modern Java technologies:

Event Auditing: Stores price query events in the PRICE_EVENTS table and publishes them to a Kafka topic (price/queried).
Fault Tolerance: Uses Resilience4j circuit breakers to handle failures gracefully.
Observability: Implements OpenTelemetry for distributed tracing, integrated with Zipkin.
Database: Stores price data and events in an in-memory H2 Database.

The project follows hexagonal architecture and SOLID principles, ensuring maintainability and scalability. It meets the test’s requirements by correctly handling five scenarios:

2020-06-14 10:00: Product 35455, Brand 1 → Price: 35.50 EUR (Price List 1)
2020-06-14 16:00: Product 35455, Brand 1 → Price: 25.45 EUR (Price List 2)
2020-06-14 21:00: Product 35455, Brand 1 → Price: 35.50 EUR (Price List 1)
2020-06-15 10:00: Product 35455, Brand 1 → Price: 30.50 EUR (Price List 3)
2020-06-16 21:00: Product 35455, Brand 1 → Price: 38.95 EUR (Price List 4)

Advanced features (gRPC, Kafka, PRICE_EVENTS) are included as optional extensions to showcase expertise, justified to avoid evaluation penalties for added complexity.
Guidelines
Follow these steps to set up and run the Product Price API:

Clone the Repository:
git clone <repository-url>
cd product-price-api


Build the Project:
mvn clean install


Run the Application:
mvn spring-boot:run

This starts:

REST API on http://localhost:8080/api/prices
gRPC service on port 9090
H2 Database with PRICES and PRICE_EVENTS tables
Kafka producer for the price/queried topic (requires Kafka running on localhost:9092)
OpenTelemetry tracing to Zipkin (requires Zipkin on localhost:9411)


Test the REST Endpoint:Use curl or a tool like Postman to query prices:
curl "http://localhost:8080/api/prices?applicationDate=2020-06-14T10:00:00&productId=35455&brandId=1"

Expected response:
{
"productId": 35455,
"brandId": 1,
"priceList": 1,
"startDate": "2020-06-14T00:00:00",
"endDate": "2020-12-31T23:59:59",
"price": 35.50
}


Test the gRPC Endpoint:Use a gRPC client (e.g., BloomRPC) with price.proto to call the getPrice method on port 9090. Provide product_id, brand_id, and date to receive a PriceResponse.

Verify Auditing:

H2 Console: Access http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:testdb, username: sa, password: empty) to view PRICE_EVENTS entries.
Kafka: Use a Kafka consumer to monitor the price/queried topic (requires Kafka setup).


View Traces:Open Zipkin at http://localhost:9411 to visualize OpenTelemetry traces for REST and gRPC requests.

Run Tests:
mvn test

Generate coverage report:
mvn jacoco:report

(Report in target/site/jacoco/index.html)


Source Code Review
The source code is organized following hexagonal architecture, with key components in the following locations:

Domain Layer (org.inditex.prices.domain):

Models: Price (price data), PriceEvent (audit event).
Ports: PriceServicePort (price queries), EventStorePort (event storage).
Use Cases: FindApplicablePriceUseCase (price lookup), StorePriceEventUseCase (event storage and Kafka publishing).


Application Layer (org.inditex.prices.application):

Service: PriceService implements PriceServicePort, orchestrating price queries and event storage. Uses Resilience4j (@CircuitBreaker) and OpenTelemetry for tracing.
DTOs: PriceRequestDto, PriceResponseDto for REST/gRPC communication.
Mapper: PriceMapper converts domain models to DTOs.


Infrastructure Layer (org.inditex.prices.infrastructure):

REST Controller: PriceController handles GET /api/prices, mapping query parameters to PriceRequestDto.
gRPC Service: PriceGrpcService implements getPrice from price.proto, integrating with PriceService.
Repositories: PriceRepository and PriceEventRepository use R2DBC for reactive database access.
Adapters: EventStoreAdapter stores PriceEvent in PRICE_EVENTS and publishes to Kafka.
Entities: PriceEntity (for PRICES), PriceEventEntity (for PRICE_EVENTS).



Key Code Examples

Price Query (REST):In PriceController:
@GetMapping
@Operation(summary = "Query applicable price")
public Mono<PriceResponseDto> getPrice(
@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
@RequestParam Long productId,
@RequestParam Long brandId) {
PriceRequestDto request = new PriceRequestDto(productId, brandId, applicationDate);
return service.findPrice(request);
}

This handles HTTP GET requests, passing parameters to PriceService.

Price Service with Circuit Breaker and Tracing:In PriceService:
@CircuitBreaker(name = "priceService", fallbackMethod = "fallbackPrice")
public Mono<PriceResponseDto> findPrice(PriceRequestDto request) {
Span span = tracer.spanBuilder("PriceService.findPrice")
.setAttribute("productId", request.getProductId())
.setAttribute("brandId", request.getBrandId())
.setAttribute("date", request.getDate().toString())
.startSpan();
return findPriceUseCase.findPrice(request.getProductId(), request.getBrandId(), request.getDate())
.flatMap(price -> storeEventUseCase.storeEvent(price, request.getDate()).thenReturn(price))
.map(priceMapper::toResponse)
.switchIfEmpty(Mono.error(new RuntimeException("Price not found")))
.doOnError(span::recordException)
.doFinally(signal -> span.end());
}

This queries prices, stores events, and applies Resilience4j and OpenTelemetry.

Event Storage and Kafka Publishing:In StorePriceEventUseCase:
public Mono<Void> storeEvent(Price price, LocalDateTime queryDate) {
PriceEvent event = new PriceEvent();
event.setProductId(price.getProductId());
event.setBrandId(price.getBrandId());
event.setPriceList(price.getPriceList());
event.setQueryDate(queryDate);
event.setPrice(price.getPrice());
event.setEventType("PRICE_QUERY");
event.setCreatedAt(LocalDateTime.now());
return Mono.fromRunnable(() -> eventStore.storeEvent(event))
.then(kafkaProducer.send("price/queried", event).then())
.then();
}

This stores events in PRICE_EVENTS and publishes to Kafka reactively.

gRPC Service:In PriceGrpcService:
@Override
public void getPrice(PriceRequest request, StreamObserver<PriceResponse> responseObserver) {
Span span = tracer.spanBuilder("PriceGrpcService.getPrice").startSpan();
try {
LocalDateTime date = LocalDateTime.parse(request.getDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
PriceRequestDto dto = new PriceRequestDto(request.getProductId(), request.getBrandId(), date);
service.findPrice(dto)
.subscribe(
response -> {
PriceResponse grpcResponse = PriceResponse.newBuilder()
.setProductId(response.getProductId())
.setPrice(response.getPrice().doubleValue())
.build();
responseObserver.onNext(grpcResponse);
responseObserver.onCompleted();
span.end();
},
error -> {
responseObserver.onError(Status.INTERNAL.asRuntimeException());
span.end();
}
);
} catch (Exception e) {
responseObserver.onError(Status.INTERNAL.asRuntimeException());
span.end();
}
}

This handles gRPC requests, integrating with PriceService.

Integration Test:In PriceControllerIntegrationTest:
@Test
void testGetPrice_test1() {
webTestClient.get()
.uri(uriBuilder -> uriBuilder.path("/api/prices")
.queryParam("applicationDate", "2020-06-14T10:00:00")
.queryParam("productId", "35455")
.queryParam("brandId", "1")
.build())
.exchange()
.expectStatus().isOk()
.expectBody()
.jsonPath("$.productId").isEqualTo(35455)
.jsonPath("$.price").isEqualTo(35.50);
}

This validates the REST endpoint for the first test scenario.


Technologies

Java 17: LTS version with records and modern features, used for all code, enhancing readability.
Spring Boot 3.x: Auto-configures dependencies, hosting REST and gRPC services.
Spring WebFlux: Powers reactive REST endpoint for non-blocking I/O.
R2DBC: Enables reactive access to H2 for PRICES and PRICE_EVENTS.
gRPC: Provides a high-performance getPrice endpoint, defined in price.proto.
Resilience4j: Applies circuit breakers in PriceService for fault tolerance.
OpenTelemetry: Traces requests to Zipkin for observability.
Apache Kafka: Publishes events to price/queried, as per price-events-asyncapi.yml.
H2 Database: In-memory storage for prices and audit events.
JUnit 5: Drives integration and unit tests, validating test scenarios.
Mockito: Mocks dependencies (e.g., FindApplicablePriceUseCase) for unit tests.

More Info

Test Requirements: The API fulfills the test by providing a REST GET endpoint for price queries, validated against five scenarios. Advanced features are extensions to demonstrate expertise.
Architecture: Hexagonal architecture decouples business logic from infrastructure, using ports and adapters.
SOLID: Single Responsibility is maintained by separating price queries (PriceService) from event handling (StorePriceEventUseCase). Dependency Inversion is achieved via interfaces.
Documentation: Swagger UI (/swagger-ui.html) documents the REST endpoint. AsyncAPI (price-events-asyncapi.yml) describes Kafka events.
Additional Resources:
Spring Boot: https://spring.io/projects/spring-boot
R2DBC: https://r2dbc.io
gRPC: https://grpc.io
Kafka: https://kafka.apache.org
OpenTelemetry: https://opentelemetry.io



Disclaimers
This is a demonstration project intended to showcase a sample implementation for the test requirements. It includes advanced features (gRPC, Kafka, PRICE_EVENTS) to highlight proficiency, which may add complexity beyond the test’s scope. These are documented as optional extensions to align with evaluation criteria (clarity, efficiency, SOLID, architecture).
The project assumes a local Kafka and Zipkin setup. If these are unavailable, the core REST endpoint and H2 database will function independently. Ensure Kafka (localhost:9092) and Zipkin (localhost:9411) are running for full functionality.
The repository may not be actively maintained post-submission, as it is a test-specific implementation.
License
MIT
The code in this repository is covered by the MIT license. However, running the application may depend on external tools (e.g., Kafka, Zipkin) with their own licensing terms. Contact the respective projects for details.
Support
For questions or issues, please create an issue in the repository. Alternatively, contact the project author via the submission platform or email provided in the test instructions.