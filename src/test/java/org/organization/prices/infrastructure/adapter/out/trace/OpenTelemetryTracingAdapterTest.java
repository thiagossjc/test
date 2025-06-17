package org.organization.prices.infrastructure.adapter.out.trace;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class OpenTelemetryTracingAdapterTest {

    private Tracer tracer;
    private Span span;
    private SpanBuilder spanBuilder;
    private OpenTelemetryTracingAdapter adapter;

    @BeforeEach
    void setUp() {
        tracer = mock(Tracer.class);
        span = mock(Span.class);
        spanBuilder = mock(SpanBuilder.class);

        when(tracer.spanBuilder(anyString())).thenReturn(spanBuilder);
        when(spanBuilder.startSpan()).thenReturn(span);

        adapter = new OpenTelemetryTracingAdapter(tracer);
    }

    @Test
    void trace_withMono_shouldCreateSpan_andEmitValue() {
        // Arrange
        String operationName = "testMono";
        Mono<String> mono = Mono.just("value");

        // Act
        Mono<String> result = adapter.trace(operationName, mono, "key", "val");

        // Assert
        StepVerifier.create(result)
                .expectNext("value")
                .verifyComplete();

        verify(tracer).spanBuilder(operationName);
        verify(spanBuilder).startSpan();
        verify(span).setAttribute("key", "val");
        verify(span).end();
    }

    @Test
    void trace_withMono_shouldRecordException() {
        // Arrange
        String operationName = "errorMono";
        RuntimeException error = new RuntimeException("boom");
        Mono<String> mono = Mono.error(error);

        // Act
        Mono<String> result = adapter.trace(operationName, mono);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(e -> e.getMessage().equals("boom"))
                .verify();

        verify(span).recordException(error);
        verify(span).end();
    }

    @Test
    void traceFlux_shouldCreateSpan_andEmitValues() {
        // Arrange
        String operationName = "testFlux";
        Flux<String> flux = Flux.just("A", "B");

        // Act
        Flux<String> result = adapter.traceFlux(operationName, flux, "a", "1", "b", "2");

        // Assert
        StepVerifier.create(result)
                .expectNext("A", "B")
                .verifyComplete();

        verify(tracer).spanBuilder(operationName);
        verify(span).setAttribute("a", "1");
        verify(span).setAttribute("b", "2");
        verify(span).end();
    }

    @Test
    void traceFlux_shouldRecordException() {
        // Arrange
        String operationName = "errorFlux";
        RuntimeException error = new RuntimeException("flux error");
        Flux<String> flux = Flux.concat(Flux.just("X"), Flux.error(error));

        // Act
        Flux<String> result = adapter.traceFlux(operationName, flux);

        // Assert
        StepVerifier.create(result)
                .expectNext("X")
                .expectErrorMatches(e -> e.getMessage().equals("flux error"))
                .verify();

        verify(span).recordException(error);
        verify(span).end();
    }
}
