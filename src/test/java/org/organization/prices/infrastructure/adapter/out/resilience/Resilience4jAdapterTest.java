package org.organization.prices.infrastructure.adapter.out.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.organization.prices.domain.execption.PriceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Unit tests for {@link Resilience4jAdapter}.
 */
class Resilience4jAdapterTest {

    private Resilience4jAdapter adapter;

    @BeforeEach
    void setUp() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        adapter = new Resilience4jAdapter(registry);
    }

    @Test
    void executeCircuitBreaker_withMono_shouldEmitValue() {
        String operationName = "testMonoOperation";
        Mono<String> originalMono = Mono.just("OK");

        Mono<String> result = adapter.executeCircuitBreaker(operationName, originalMono, PriceNotFoundException.class);

        StepVerifier.create(result)
                .expectNext("OK")
                .verifyComplete();
    }

    @Test
    void executeCircuitBreaker_withMono_shouldPropagateIgnoredException() {
        String operationName = "failMonoOperation";
        Mono<String> failingMono = Mono.error(new PriceNotFoundException("No price found"));

        Mono<String> result = adapter.executeCircuitBreaker(operationName, failingMono, PriceNotFoundException.class);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PriceNotFoundException
                        && throwable.getMessage().equals("No price found"))
                .verify();
    }

    @Test
    void executeCircuitBreaker_withMono_shouldEmitErrorOnOtherFailure() {
        String operationName = "failMonoOperation";
        Mono<String> failingMono = Mono.error(new IllegalStateException("original error"));

        Mono<String> result = adapter.executeCircuitBreaker(operationName, failingMono, PriceNotFoundException.class);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Service unavailable, please try again later"))
                .verify();
    }

    @Test
    void executeCircuitBreaker_withFlux_shouldEmitValues() {
        String operationName = "testFluxOperation";
        Flux<String> originalFlux = Flux.just("A", "B");

        Flux<String> result = adapter.executeCircuitBreaker(operationName, originalFlux, PriceNotFoundException.class);

        StepVerifier.create(result)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void executeCircuitBreaker_withFlux_shouldPropagateIgnoredException() {
        String operationName = "failFluxOperation";
        Flux<String> failingFlux = Flux.error(new PriceNotFoundException("No prices found"));

        Flux<String> result = adapter.executeCircuitBreaker(operationName, failingFlux, PriceNotFoundException.class);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PriceNotFoundException
                        && throwable.getMessage().equals("No prices found"))
                .verify();
    }

    @Test
    void executeCircuitBreaker_withFlux_shouldEmitErrorOnOtherFailure() {
        String operationName = "failFluxOperation";
        Flux<String> failingFlux = Flux.error(new RuntimeException("original failure"));

        Flux<String> result = adapter.executeCircuitBreaker(operationName, failingFlux, PriceNotFoundException.class);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Service unavailable, please try again later"))
                .verify();
    }
}