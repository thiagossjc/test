package org.inditex.prices.infrastructure.adapter.out.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import org.inditex.prices.application.port.CircuitBreakerPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Adapter implementation of {@link CircuitBreakerPort} using Resilience4j.
 * <p>
 * This class provides reactive circuit breaker support for Mono and Flux
 * operations by delegating to Resilience4j's CircuitBreakerRegistry.
 * </p>
 */
@Component
@Slf4j
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
     * Executes a reactive Mono operation within a circuit breaker, handling a specific error class.
     *
     * @param operationName the name of the operation for tracing or logging purposes
     * @param operation the Mono operation to be executed
     * @param classNameError the class of the error to be handled or ignored by the circuit breaker
     * @param <T> the type of the operation result
     * @return a Mono emitting the result of the operation, wrapped with circuit breaker behavior
     */
    @Override
    public <T> Mono<T> executeCircuitBreaker(String operationName, Mono<T> operation, Class<? extends Throwable> classNameError) {
        CircuitBreaker circuitBreaker = getOrCreateCircuitBreaker(operationName, classNameError);
        return Mono.fromCallable(() -> circuitBreaker)
                .flatMap(cb -> operation.transformDeferred(CircuitBreakerOperator.of(cb)))
                .doOnError(e -> log.error("Error detectado [{}]: {}", e.getClass().getSimpleName(), e.getMessage()))
                .onErrorResume(Throwable.class, e -> {
                    log.warn("Recuperando de error [{}], lanzando error personalizado", e.getClass().getSimpleName());
                    return Mono.error(new RuntimeException("Service unavailable, please try again later", e));
                });
    }

    /**
     * Executes a reactive Flux operation within a circuit breaker, handling a specific error class.
     *
     * @param operationName the name of the operation for tracing or logging purposes
     * @param operation the Flux operation to be executed
     * @param classNameError the class of the error to be handled or ignored by the circuit breaker
     * @param <T> the type of the elements emitted by the Flux operation
     * @return a Flux emitting the results of the operation, wrapped with circuit breaker behavior
     */
    @Override
    public <T> Flux<T> executeCircuitBreaker(String operationName, Flux<T> operation, Class<? extends Throwable> classNameError) {
        CircuitBreaker circuitBreaker = getOrCreateCircuitBreaker(operationName, classNameError);
        return Flux.from(operation.transformDeferred(CircuitBreakerOperator.of(circuitBreaker)))
                .doOnError(e -> log.error("Error detectado [{}]: {}", e.getClass().getSimpleName(), e.getMessage()))
                .onErrorResume(Throwable.class, e -> {
                    log.warn("Recuperando de error [{}], lanzando error personalizado", e.getClass().getSimpleName());
                    return Flux.error(new RuntimeException("Service unavailable, please try again later", e));
                });
    }

    /**
     * Creates or retrieves a circuit breaker instance for the specified operation name,
     * configured to ignore the provided error class.
     *
     * @param operationName the name of the operation
     * @param classNameError the class of the error to be ignored by the circuit breaker
     * @return a configured instance of CircuitBreaker
     */
    private CircuitBreaker getOrCreateCircuitBreaker(String operationName, Class<? extends Throwable> classNameError) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .ignoreExceptions(classNameError)
                .slowCallDurationThreshold(Duration.ofSeconds(30))
                .build();

        return circuitBreakerRegistry.circuitBreaker(operationName, config);
    }
}