package org.inditex.prices.infrastructure.adapter.in.grpc;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import org.inditex.prices.application.dto.PriceResponseDto;
import org.inditex.prices.application.port.PriceServicePort;
import org.inditex.prices.infrastructure.adapter.grpc.PriceRequest;
import org.inditex.prices.infrastructure.adapter.grpc.PriceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link PriceGrpcServiceAdapter} class.
 * Tests the gRPC service adapter's functionality for handling price requests and error scenarios.
 */
class PriceGrpcServiceAdapterUnitTest {

    /**
     * Mock for the price service port to simulate price-related operations.
     */
    @Mock
    private PriceServicePort service;

    /**
     * Mock for the OpenTelemetry tracer to simulate tracing operations.
     */
    @Mock
    private Tracer tracer;

    /**
     * Mock for the OpenTelemetry span builder to simulate span creation.
     */
    @Mock
    private SpanBuilder spanBuilder;

    /**
     * Mock for the OpenTelemetry span to simulate tracing span operations.
     */
    @Mock
    private Span span;

    /**
     * Mock for the gRPC stream observer to simulate response handling.
     */
    @Mock
    private StreamObserver<PriceResponse> responseObserver;

    /**
     * Instance of {@link PriceGrpcServiceAdapter} with injected mocks for testing.
     */
    @InjectMocks
    private PriceGrpcServiceAdapter grpcService;

    /**
     * Sets up the test environment by initializing mocks and configuring tracing behavior.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(tracer.spanBuilder(anyString())).thenReturn(spanBuilder);
        when(spanBuilder.setAttribute(anyString(), anyLong())).thenReturn(spanBuilder);
        when(spanBuilder.setAttribute(anyString(), anyString())).thenReturn(spanBuilder);
        when(spanBuilder.startSpan()).thenReturn(span);

        when(span.recordException(any(Throwable.class))).thenReturn(span);
        doNothing().when(span).end();
    }

    /**
     * Tests the {@link PriceGrpcServiceAdapter#getPrice(PriceRequest, StreamObserver)} method
     * to ensure it correctly retrieves a price and sends a {@link PriceResponse} to the observer.
     * Verifies that the service is called with the correct parameters and the response is completed.
     */
    @Test
    void getPrice_returnsPriceResponse() {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String time = now.format(DateTimeFormatter.ofPattern("HH:mm"));

        PriceResponseDto dto = PriceResponseDto.builder()
                .productId(1L)
                .brandId(1L)
                .priceList(1)
                .startDate(now.minusDays(1).toString())
                .endDate(now.plusDays(1).toString())
                .price(java.math.BigDecimal.valueOf(99.99))
                .build();

        when(service.findPrice(eq(1L), eq(1L), any(LocalDateTime.class)))
                .thenReturn(Mono.just(dto));

        grpcService.getPrice(
                PriceRequest.newBuilder()
                        .setProductId(1L)
                        .setBrandId(1L)
                        .setDate(date)
                        .setTime(time)
                        .build(),
                responseObserver
        );

        verify(service).findPrice(eq(1L), eq(1L), any(LocalDateTime.class));
        verify(responseObserver).onNext(any(PriceResponse.class));
        verify(responseObserver).onCompleted();
    }

    /**
     * Tests the {@link PriceGrpcServiceAdapter#getPrice(PriceRequest, StreamObserver)} method
     * when no price is found. Verifies that the observer's {@code onError} method is called
     * with an appropriate exception.
     */
    @Test
    void getPrice_whenPriceNotFound_callsOnError() {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String time = now.format(DateTimeFormatter.ofPattern("HH:mm"));

        when(service.findPrice(eq(1L), eq(1L), any(LocalDateTime.class)))
                .thenReturn(Mono.empty());

        grpcService.getPrice(
                PriceRequest.newBuilder()
                        .setProductId(1L)
                        .setBrandId(1L)
                        .setDate(date)
                        .setTime(time)
                        .build(),
                responseObserver
        );

        verify(responseObserver).onError(any(Throwable.class));
    }
}