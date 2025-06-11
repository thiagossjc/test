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
 */
class PriceGrpcServiceAdapterUnitTest {

    @Mock
    private PriceServicePort service;

    @Mock
    private Tracer tracer;

    @Mock
    private SpanBuilder spanBuilder;

    @Mock
    private Span span;

    @Mock
    private StreamObserver<PriceResponse> responseObserver;

    @InjectMocks
    private PriceGrpcServiceAdapter grpcService;

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

        when(service.findApplicablePrice(eq(1L), eq(1L), any(LocalDateTime.class)))
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

        verify(service).findApplicablePrice(eq(1L), eq(1L), any(LocalDateTime.class));
        verify(responseObserver).onNext(any(PriceResponse.class));
        verify(responseObserver).onCompleted();
    }

    @Test
    void getPrice_whenPriceNotFound_callsOnError() {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String time = now.format(DateTimeFormatter.ofPattern("HH:mm"));

        when(service.findApplicablePrice(eq(1L), eq(1L), any(LocalDateTime.class)))
                .thenReturn(Mono.error(new RuntimeException("Service unavailable, please try again later")));

        grpcService.getPrice(
                PriceRequest.newBuilder()
                        .setProductId(1L)
                        .setBrandId(1L)
                        .setDate(date)
                        .setTime(time)
                        .build(),
                responseObserver
        );

        verify(service).findApplicablePrice(eq(1L), eq(1L), any(LocalDateTime.class));
        verify(responseObserver).onError(any(Throwable.class));
        verify(span).recordException(any(Throwable.class));
    }
}