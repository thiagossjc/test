package org.organization.prices.application.port;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Port interface for executing operations protected by a circuit breaker pattern.
 */
public interface CircuitBreakerPort {

    /**
     * Executes a reactive Mono operation within a circuit breaker, handling a specific error class.
     *
     * @param operationName the name of the operation for tracing or logging purposes
     * @param operation the Mono operation to be executed
     * @param classNameError the class of the error to be handled or ignored by the circuit breaker
     * @param <T> the type of the operation result
     * @return a Mono emitting the result of the operation, wrapped with circuit breaker behavior
     */
    <T> Mono<T> executeCircuitBreaker(String operationName, Mono<T> operation, Class<? extends Throwable> classNameError);

    /**
     * Executes a reactive Flux operation within a circuit breaker, handling a specific error class.
     *
     * @param operationName the name of the operation for tracing or logging purposes
     * @param operation the Flux operation to be executed
     * @param classNameError the class of the error to be handled or ignored by the circuit breaker
     * @param <T> the type of the elements emitted by the Flux operation
     * @return a Flux emitting the results of the operation, wrapped with circuit breaker behavior
     */
    <T> Flux<T> executeCircuitBreaker(String operationName, Flux<T> operation, Class<? extends Throwable> classNameError);
}