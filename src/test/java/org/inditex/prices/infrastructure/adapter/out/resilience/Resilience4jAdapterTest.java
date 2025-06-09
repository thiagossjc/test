package org.inditex.prices.infrastructure.adapter.out.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link Resilience4jAdapter}.
 * <p>
 * This class validates the behavior of the adapter that applies
 * Resilience4j circuit breaker logic to Mono and Flux operations.
 * </p>
 */
class Resilience4jAdapterTest {

    /**
     * Mocked circuit breaker registry used to retrieve circuit breakers by name.
     */
    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    /**
     * Mocked circuit breaker to simulate circuit breaker behavior.
     */
    @Mock
    private CircuitBreaker circuitBreaker;

    /**
     * The adapter under test.
     */
    private Resilience4jAdapter adapter;

    /**
     * Initializes mocks and the adapter before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new Resilience4jAdapter(circuitBreakerRegistry);
    }

    /**
     * Tests that the Mono operation is passed through the circuit breaker
     * and emits the expected result.
     */
    @Test
    void executeCircuitBreaker_withMono_shouldEmitValue() {
        // Arrange
        String operationName = "testMonoOperation";
        Mono<String> originalMono = Mono.just("OK");

        // Use a real CircuitBreaker with a config
        CircuitBreakerRegistry realRegistry = CircuitBreakerRegistry.ofDefaults();
        Resilience4jAdapter realAdapter = new Resilience4jAdapter(realRegistry);

        // Act
        Mono<String> result = realAdapter.executeCircuitBreaker(operationName, originalMono);

        // Assert
        StepVerifier.create(result)
                .expectNext("OK")
                .verifyComplete();
    }

    /**
     * Tests that the Mono operation fails and emits a custom fallback error
     * when an exception occurs.
     */
    @Test
    void executeCircuitBreaker_withMono_shouldEmitErrorOnFailure() {
        // Arrange
        String operationName = "failMonoOperation";
        Mono<String> failingMono = Mono.error(new IllegalStateException("original error"));

        when(circuitBreakerRegistry.circuitBreaker(operationName)).thenReturn(circuitBreaker);

        // Act
        Mono<String> result = adapter.executeCircuitBreaker(operationName, failingMono);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Service unavailable, please try again later"))
                .verify();

        verify(circuitBreakerRegistry, times(1)).circuitBreaker(operationName);
    }

    /**
     * Tests that the Flux operation is passed through the circuit breaker
     * and emits all expected values.
     */
    @Test
    void executeCircuitBreaker_withFlux_shouldEmitValues() {
        // Arrange
        String operationName = "testFluxOperation";
        Flux<String> originalFlux = Flux.just("A", "B");

        // Usa el CircuitBreakerRegistry real (sin mock)
        CircuitBreakerRegistry realRegistry = CircuitBreakerRegistry.ofDefaults();
        Resilience4jAdapter realAdapter = new Resilience4jAdapter(realRegistry);

        // Act
        Flux<String> result = realAdapter.executeCircuitBreaker(operationName, originalFlux);

        // Assert
        StepVerifier.create(result)
                .expectNext("A", "B")
                .verifyComplete();
    }

    /**
     * Tests that the Flux operation fails and emits a custom fallback error
     * when an exception occurs.
     */
    @Test
    void executeCircuitBreaker_withFlux_shouldEmitErrorOnFailure() {
        // Arrange
        String operationName = "failFluxOperation";
        Flux<String> failingFlux = Flux.error(new RuntimeException("original failure"));

        when(circuitBreakerRegistry.circuitBreaker(operationName)).thenReturn(circuitBreaker);

        // Act
        Flux<String> result = adapter.executeCircuitBreaker(operationName, failingFlux);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Service unavailable, please try again later"))
                .verify();

        verify(circuitBreakerRegistry, times(1)).circuitBreaker(operationName);
    }
}
