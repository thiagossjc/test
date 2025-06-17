package org.organization.prices.infrastructure.adapter.out.trace;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.organization.prices.application.port.TracePort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter implementation of {@link TracePort} using OpenTelemetry API.
 * <p>
 * Provides reactive tracing capabilities for Mono and Flux operations
 * by creating and managing spans using OpenTelemetry's Tracer.
 * </p>
 */
@Component
public class OpenTelemetryTracingAdapter implements TracePort {

    /**
     * OpenTelemetry tracer used to create spans.
     */
    private final Tracer tracer;

    /**
     * Constructs a new {@code OpenTelemetryTracingAdapter} with the given tracer.
     *
     * @param tracer the OpenTelemetry tracer instance
     */
    public OpenTelemetryTracingAdapter(Tracer tracer) {
        this.tracer = tracer;
    }

    /**
     * Traces the execution of a {@link Mono} operation with the specified operation name and optional attributes.
     * <p>
     * A new span is started before subscription and ended after termination. Exceptions during the operation
     * are recorded on the span.
     * </p>
     *
     * @param operationName the name of the trace operation (span)
     * @param operation     the reactive Mono operation to be traced
     * @param attributes    optional key-value attribute pairs to add to the span (even indices are keys, odd indices are values)
     * @param <T>           the type emitted by the Mono
     * @return a traced Mono emitting the original operation's signals
     */
    @Override
    public <T> Mono<T> trace(String operationName, Mono<T> operation, String... attributes) {
        return Mono.defer(() -> {
            Span span = tracer.spanBuilder(operationName).startSpan();
            for (int i = 0; i < attributes.length - 1; i += 2) {
                span.setAttribute(attributes[i], attributes[i + 1]);
            }
            return operation
                    .doOnError(span::recordException)
                    .doFinally(signal -> span.end());
        });
    }

    /**
     * Traces the execution of a {@link Flux} operation with the specified operation name and optional attributes.
     * <p>
     * A new span is started before subscription and ended after termination. Exceptions during the operation
     * are recorded on the span.
     * </p>
     *
     * @param operationName the name of the trace operation (span)
     * @param flux          the reactive Flux operation to be traced
     * @param attributes    optional key-value attribute pairs to add to the span (even indices are keys, odd indices are values)
     * @param <T>           the type emitted by the Flux
     * @return a traced Flux emitting the original operation's signals
     */
    @Override
    public <T> Flux<T> traceFlux(String operationName, Flux<T> flux, String... attributes) {
        return Flux.defer(() -> {
            Span span = tracer.spanBuilder(operationName).startSpan();
            for (int i = 0; i < attributes.length - 1; i += 2) {
                span.setAttribute(attributes[i], attributes[i + 1]);
            }
            return flux
                    .doOnError(span::recordException)
                    .doFinally(signalType -> span.end());
        });
    }
}