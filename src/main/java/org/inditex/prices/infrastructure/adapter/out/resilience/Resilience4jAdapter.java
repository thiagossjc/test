package org.inditex.prices.infrastructure.adapter.out.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.inditex.prices.application.port.CircuitBreakerPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter implementation of {@link CircuitBreakerPort} using Resilience4j.
 * <p>
 * This class provides reactive circuit breaker support for Mono and Flux
 * operations by delegating to Resilience4j's CircuitBreakerRegistry.
 * </p>
 */
@Component
public class Resilience4jAdapter implements CircuitBreakerPort {

    /**
     * Registry for managing and retrieving circuit breakers by name.
     */
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    /**
     * Constructs a new {@code Resilience4jAdapter} with the given circuit breaker registry.
     *
     * @param circuitBreakerRegistry the registry used to retrieve circuit breakers
     */
    public Resilience4jAdapter(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    /**
     * Executes the given {@link Mono} operation protected by a circuit breaker
     * identified by the given operation name.
     * <p>
     * In case of failure, it returns a Mono error with a standardized message.
     * </p>
     *
     * @param operationName the name of the circuit breaker operation
     * @param operation     the reactive Mono operation to execute
     * @param <T>           the type emitted by the Mono
     * @return a Mono emitting the result or an error if the circuit breaker is open or the operation fails
     */
    @Override
    public <T> Mono<T> executeCircuitBreaker(String operationName, Mono<T> operation) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(operationName);
        return operation.transform(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(throwable -> Mono.error(new RuntimeException("Service unavailable, please try again later", throwable)));
    }

    /**
     * Executes the given {@link Flux} operation protected by a circuit breaker
     * identified by the given operation name.
     * <p>
     * In case of failure, it returns a Flux error with a standardized message.
     * </p>
     *
     * @param operationName the name of the circuit breaker operation
     * @param operation     the reactive Flux operation to execute
     * @param <T>           the type emitted by the Flux
     * @return a Flux emitting the results or an error if the circuit breaker is open or the operation fails
     */
    @Override
    public <T> Flux<T> executeCircuitBreaker(String operationName, Flux<T> operation) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(operationName);
        return operation
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(throwable -> Flux.error(new RuntimeException("Service unavailable, please try again later", throwable)));
    }
}