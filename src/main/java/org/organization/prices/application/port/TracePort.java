package org.organization.prices.application.port;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interface for tracing reactive operations using observability tools like OpenTelemetry.
 */
public interface TracePort {

    /**
     * Traces a Mono operation with the specified operation name and optional attributes.
     *
     * @param operationName the name of the traced operation
     * @param operation the reactive Mono operation to trace
     * @param attributes optional key-value pairs to add as attributes (should be passed in key1, value1, key2, value2... order)
     * @param <T> the type of the Mono result
     * @return a traced Mono operation
     */
    <T> Mono<T> trace(String operationName, Mono<T> operation, String... attributes);

    /**
     * Traces a Flux operation with the specified operation name and optional tags.
     *
     * @param name the name of the traced operation
     * @param flux the reactive Flux operation to trace
     * @param tags optional key-value pairs to add as tags (should be passed in key1, value1, key2, value2... order)
     * @param <T> the type of the Flux result
     * @return a traced Flux operation
     */
    <T> Flux<T> traceFlux(String name, Flux<T> flux, String... tags);
}
